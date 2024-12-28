package com.usth.chat_app_api.config_websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.usth.chat_app_api.attachment.Attachment;
import com.usth.chat_app_api.aws.IAwsSNSService;
import com.usth.chat_app_api.message.Message;
import com.usth.chat_app_api.message.MessageDTO;
import com.usth.chat_app_api.message.MessageService;
import com.usth.chat_app_api.user_info.IUserInfoService;
import com.usth.chat_app_api.user_info.UserInfo;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class MyHandler extends AbstractWebSocketHandler {

    @Autowired
    private MessageService messageService;

    @Autowired
    private WebSocketSessionManager sessionManager;

    @Autowired
    private IUserInfoService userInfoService;

    @Autowired
    IAwsSNSService awsSNSService;



    private final ObjectMapper objectMapper = new ObjectMapper();

//    @Override
//    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
//        // register module for objectMapper
//        objectMapper.registerModule(new JavaTimeModule());
//
//        MessageDTO messageDTO = objectMapper.readValue(message.getPayload(), MessageDTO.class);
//
//        Long userId = messageDTO.getUserId();
//
////        messageService.sendMessage(userId, messageDTO.getConversationId(), messageDTO.getContent());
//
//        List<UserInfo> participants = messageService.getParticipantsByConversationId(messageDTO.getConversationId());
//
//        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//
//        for (UserInfo participant : participants) {
//            if (!participant.getId().equals(userId)) {
//                List<WebSocketSession> participantSessions = sessionManager.getSessions(participant.getId());
//
//                if (participantSessions != null) {
//                    for (WebSocketSession participantSession : participantSessions) {
//                        if (participantSession != null && participantSession.isOpen()) {
//                            UserInfo sender = userInfoService.findUserInforById(userId);
//
//                            if (sender != null) {
//                                String senderName = sender.getFirstName() + " " + sender.getLastName();
//
//                                Map<String, String> messageData = new HashMap<>();
//                                messageData.put("sender", senderName);
//                                messageData.put("content", messageDTO.getContent());
//                                messageData.put("timestamp", timestamp);
//
//                                String messageJson = objectMapper.writeValueAsString(messageData);
//
//                                participantSession.sendMessage(new TextMessage(messageJson));
//
//                                log.info("Message sent to participant: " + participant.getId());
//                            } else {
//                                log.error("Sender with userId " + userId + " not found in database.");
//                            }
//                        } else {
//                            sessionManager.removeSession(participant.getId(), participantSession);
//                            log.info("Removed closed session for participant: " + participant.getId());
//                        }
//                    }
//                }
//            }
//        }
//
////        session.sendMessage(new TextMessage("Server confirm: " + messageDTO.getContent()));
////        session.sendMessage(new TextMessage(messageJson));
//    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // register module for objectMapper
        objectMapper.registerModule(new JavaTimeModule());

        MessageDTO messageDTO = objectMapper.readValue(message.getPayload(), MessageDTO.class);

        // get params
        Long userId = messageDTO.getUserId();
        List<Attachment> attachments = messageDTO.getAttachments();
        LocalDateTime messageTime = messageDTO.getMessageTime() == null ?
                LocalDateTime.now() : messageDTO.getMessageTime();

        // save message into DB
        Message savedMessage = messageService.saveMessage(userId, messageDTO.getConversationId(), messageDTO.getContent(), messageTime, attachments);

        // find UserInfo
        UserInfo sender = userInfoService.findUserInforById(userId);

        // push notification body
        String pushNotificationBody = null;

        // prepare message to send
        String messageJson = null;
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Date.from(messageTime.atZone(ZoneId.systemDefault()).toInstant()));

        if (sender != null) {
            String senderName = sender.getFirstName() + " " + sender.getLastName();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("id", savedMessage.getId());
            objectNode.put("userId", userId);
            objectNode.put("senderName", senderName);
            objectNode.put("conversationName", messageDTO.getConversationName());
            objectNode.put("conversationId", messageDTO.getConversationId());
            objectNode.put("content", messageDTO.getContent());
            objectNode.put("messageTime", timestamp);
            objectNode.putPOJO("attachments", attachments);

//            // Nếu có attachments, thêm chúng vào JSON
//            if (attachments != null && !attachments.isEmpty()) {
//                objectNode.putPOJO("attachments", attachments);
//            }

            messageJson = objectMapper.writeValueAsString(objectNode);

            if(messageDTO.getContent() != null){
                if(!messageDTO.getContent().isEmpty()){
                    pushNotificationBody = sender.getUsername() + ": "+ messageDTO.getContent();
                }
            }
        }

        final String finalPushNotificationBody = pushNotificationBody;

        List<UserInfo> participants = messageService.getParticipantsByConversationId(messageDTO.getConversationId());

        for (UserInfo participant : participants) {
            if (!participant.getId().equals(userId)) {
                List<WebSocketSession> participantSessions = sessionManager.getSessions(participant.getId());

                if (participantSessions != null) {
                    for (WebSocketSession participantSession : participantSessions) {
                        if (participantSession != null && participantSession.isOpen()) {
                            if (messageJson != null) {
                                // send message
                                participantSession.sendMessage(new TextMessage(messageJson));
                                // push notification
                                if(participant.getDeviceToken() != null ||
                                        !participant.getDeviceToken().isEmpty() && finalPushNotificationBody != null){
                                    CompletableFuture.runAsync(() ->
                                            awsSNSService.publishNotification(participant.getDeviceToken(),messageDTO.getConversationName(),
                                                    finalPushNotificationBody, messageDTO.getConversationId()));
                                }
                                log.info("Message sent to participant: " + participant.getId());
                            } else {
                                log.error("Sender with userId " + userId + " not found in database.");
                            }
                        } else {
                            sessionManager.removeSession(participant.getId(), participantSession);
                            log.info("Removed closed session for participant: " + participant.getId());
                        }
                    }
                }
            }
        }

//        assert messageJson != null;
//        session.sendMessage(new TextMessage(messageJson));
    }

    // Handle BinaryMessage
    @Override
    protected void handleBinaryMessage(@NonNull WebSocketSession session,@NonNull BinaryMessage message) throws Exception {
        System.out.println("Handler Binary message");
        System.out.println(message.getPayload());

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Connection established: " + session.getId());

        String query = session.getUri().getQuery();

        Map<String, String> queryParams = getQueryParams(query);
        String userIdStr = queryParams.get("userId");

        if (userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);

                sessionManager.addSession(userId, session);

                log.info("Session added for userId: " + userId);
            } catch (NumberFormatException e) {
                log.error("Invalid userId in query parameters: " + userIdStr, e);
                session.close(CloseStatus.BAD_DATA);
            }
        } else {
            log.warn("No userId found in query parameters");
            session.close(CloseStatus.BAD_DATA);
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        Long userId = sessionManager.getUserIdBySession(session);

        if (userId != null) {
            sessionManager.removeSession(userId, session);
            log.info("Session closed and removed for userId: {} - Because: {}", userId, status.getReason());
        } else {
            log.warn("Failed to remove session: userId not found for session " + session.getId());
        }
    }
    private Map<String, String> getQueryParams(String query) {
        Map<String, String> queryParams = new HashMap<>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length > 1) {
                    queryParams.put(keyValue[0], keyValue[1]);
                } else {
                    queryParams.put(keyValue[0], "");
                }
            }
        }
        return queryParams;
    }
}
