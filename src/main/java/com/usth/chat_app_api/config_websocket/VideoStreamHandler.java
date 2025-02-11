package com.usth.chat_app_api.config_websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.usth.chat_app_api.aws.IAwsSNSService;
import com.usth.chat_app_api.constant.ApplicationConstant;
import com.usth.chat_app_api.conversation.Conversation;
import com.usth.chat_app_api.conversation.ConversationService;
import com.usth.chat_app_api.conversation_participant.ConversationParticipant;
import com.usth.chat_app_api.user_info.UserInfo;
import com.usth.chat_app_api.utils.Helper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class VideoStreamHandler extends TextWebSocketHandler {
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private IAwsSNSService awsSNSService;
    private Map<Long, WebSocketSession> sessionsMap = new HashMap<>();
    private List<String> constList = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        try{
            if(session.getUri() != null){
                String queryParams = session.getUri().getQuery();
                Long userId = Long.parseLong(Helper.parseWsQuery(queryParams).toString());

                if(!sessionsMap.containsKey(userId)){
                    sessionsMap.put(userId, session);
                    log.info("Video call socket connected - {}. Total session: {} ", userId, sessionsMap.keySet().size());
                } else {
                    log.error("Socket UserId "+ userId + " already exist! --> Add again");
                    sessionsMap.remove(userId); // remove
                    sessionsMap.put(userId, session); // add again
                }
            } else{
                log.error("Video call socket connection fail");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        removeSession(session);
    }

    private void removeSession(WebSocketSession session){
        try{
            if(session.getUri() != null){
                String queryParams = session.getUri().getQuery();
                Long userId = Long.parseLong(Helper.parseWsQuery(queryParams).toString());

                if(sessionsMap.containsKey(userId)){
                    sessionsMap.remove(userId);
                    log.info("Video call socket terminate user {} - Total session: {} ",userId, sessionsMap.keySet().size());
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    * If "action" is not null, we send answer to the caller
    * Else we push call notification to the receiver
    */
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        ObjectMapper objectMapper = new ObjectMapper();

        Long userId;
        Long conversationId;
        String conversationName;
        String callType;
        try{
            // get request
            String payload = message.getPayload();
            JsonNode jsonNode = objectMapper.readTree(payload);

            System.out.println(payload);

            if(jsonNode.get("userId") != null && jsonNode.get("conversationId") != null && jsonNode.get("conversationName") != null){
                userId = jsonNode.get("userId").asLong(0L);
                conversationId = jsonNode.get("conversationId").asLong(0L);
                conversationName =jsonNode.get("conversationName").asText("");
                callType = jsonNode.get("callType").asText("");
                final String sdp = jsonNode.get("sdp").asText("");
                final String type = jsonNode.get("type").asText();

                // find conversation by id
                Conversation conversation = conversationService.findById(conversationId);
                if(conversation != null){
                    // get all participants in conversation except current users
                    List<UserInfo> conversationParticipants = conversation
                            .getParticipants().stream()
                            .map(ConversationParticipant::getUser)
                            .toList();

                    // if type == offer -> push notification to the receiver
                    if(type.equals("offer")){
                        for (UserInfo remoteUser: conversationParticipants){
                            if(!remoteUser.getId().equals(userId) &&
                                    (remoteUser.getDeviceToken() != null || !remoteUser.getDeviceToken().isEmpty())){
                                // push notification to remote user
                                // create body
                                ObjectNode objectNode = objectMapper.createObjectNode();
                                objectNode.put("sdp", sdp);
                                final String body = objectMapper.writeValueAsString(objectNode);
                                if(callType.equals(ApplicationConstant.VIDEO_CALL)){
                                    // send notification
                                    CompletableFuture.runAsync(() ->
                                            awsSNSService.publishNotification(remoteUser.getDeviceToken(),ApplicationConstant.VIDEO_CALL,
                                                    body, conversationId));
                                }
                            }
                        }
                    }

                    if(jsonNode.get("isOffer") != null && jsonNode.get("isOffer").asBoolean()){
                        constList.add(payload);
                        log.info("List size: " + constList.size());
                    }

                    if(jsonNode.get("action") != null && jsonNode.get("action").asText().equals(ApplicationConstant.AGREE_CALL)){
                        for(String data : constList){
                            session.sendMessage(new TextMessage(data));
                            log.info("Send ICE in constList user id {}", userId);

                        }
                    }

                    if(type.equals("candidate")){
                        // send answer
                        sendAnswer(new TextMessage(payload), conversationParticipants, null, userId);
                    } else {
                        String action = "No action";
                        if (jsonNode.get("action") != null) {
                            action = jsonNode.get("action").asText();
                        }


                        // create message
                        ObjectNode objectNode = objectMapper.createObjectNode();
                        objectNode.put("type", type);
                        objectNode.put("sdp", sdp);
                        objectNode.put("action", action);
                        String data = objectMapper.writeValueAsString(objectNode);

                        // send answer
                        sendAnswer(new TextMessage(data), conversationParticipants, action, userId);

                    }

                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    * Method used to send answer
    * Params:
    *   conversationParticipants: list of participant in a conversation except the current user
    */
    private void sendAnswer(TextMessage data, List<UserInfo> conversationParticipants, String action, Long currentUserId){
        log.info("On sendAnswer");
        try{
            for(UserInfo remoteUser: conversationParticipants){
                Long remoteUserId = remoteUser.getId();

                if(sessionsMap.get(remoteUserId) != null && !remoteUserId.equals(currentUserId)){
                    WebSocketSession session = sessionsMap.get(remoteUserId);
                    session.sendMessage(data);
                    log.info("Sent message to username: {} - userId: {}", remoteUser.getUsername(), remoteUserId);
                }
            }

            // close socket if the answer is DECLINE
            if(action != null && action.equals(ApplicationConstant.DECLINE_CALL)){
                if(sessionsMap.get(currentUserId) != null){
                    removeSession(sessionsMap.get(currentUserId));
                }
            }
        } catch (Exception e){
            log.error("Cannot send data to remote user because " + e.getMessage());
        }
    }
}
