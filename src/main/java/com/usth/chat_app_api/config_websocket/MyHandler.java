package com.usth.chat_app_api.config_websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usth.chat_app_api.message.MessageDTO;
import com.usth.chat_app_api.message.MessageService;
import com.usth.chat_app_api.user_info.IUserInfoService;
import com.usth.chat_app_api.user_info.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class MyHandler extends TextWebSocketHandler {

    @Autowired
    private MessageService messageService;

    @Autowired
    private WebSocketSessionManager sessionManager;

    @Autowired
    private IUserInfoService userInfoService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {

        MessageDTO messageDTO = objectMapper.readValue(message.getPayload(), MessageDTO.class);

        Long userId = messageDTO.getUserId();

        messageService.sendMessage(userId, messageDTO.getConversationId(), messageDTO.getContent());

        List<UserInfo> participants = messageService.getParticipantsByConversationId(messageDTO.getConversationId());

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        for (UserInfo participant : participants) {
            if (!participant.getId().equals(userId)) {
                List<WebSocketSession> participantSessions = sessionManager.getSessions(participant.getId());

                if (participantSessions != null) {
                    for (WebSocketSession participantSession : participantSessions) {
                        if (participantSession != null && participantSession.isOpen()) {
                            UserInfo sender = userInfoService.findUserInforById(userId);

                            if (sender != null) {
                                String senderName = sender.getFirstName() + " " + sender.getLastName();

                                Map<String, String> messageData = new HashMap<>();
                                messageData.put("sender", senderName);
                                messageData.put("content", messageDTO.getContent());
                                messageData.put("timestamp", timestamp);

                                String messageJson = objectMapper.writeValueAsString(messageData);

                                participantSession.sendMessage(new TextMessage(messageJson));

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

        session.sendMessage(new TextMessage("Server confirm: " + messageDTO.getContent()));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Log the connection establishment
        log.info("Connection established: " + session.getId());

        // Extract the query parameters from the URI
        String query = session.getUri().getQuery();

        // Parse the query parameters to get the userId
        Map<String, String> queryParams = getQueryParams(query);
        String userIdStr = queryParams.get("userId");

        if (userIdStr != null) {
            try {
                // Parse userId from String to Long
                Long userId = Long.parseLong(userIdStr);

                // Add the session to the sessionManager with the userId
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

        Long userId = sessionManager.getUserIdBySession(session);  // Lấy userId từ session manager

        if (userId != null) {
            sessionManager.removeSession(userId, session);
            log.info("Session closed and removed for userId: " + userId);
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
