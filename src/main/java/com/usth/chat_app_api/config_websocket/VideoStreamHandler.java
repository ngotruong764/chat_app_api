package com.usth.chat_app_api.config_websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.usth.chat_app_api.aws.IAwsSNSService;
import com.usth.chat_app_api.call_status.CallStatus;
import com.usth.chat_app_api.call_status.ICallStatusService;
import com.usth.chat_app_api.call_type.CallType;
import com.usth.chat_app_api.call_type.ICallTypeService;
import com.usth.chat_app_api.constant.ApplicationConstant;
import com.usth.chat_app_api.conversation.Conversation;
import com.usth.chat_app_api.conversation.ConversationService;
import com.usth.chat_app_api.conversation_participant.ConversationParticipant;
import com.usth.chat_app_api.ice_candidate.ICECandidate;
import com.usth.chat_app_api.ice_candidate.IICECandidateService;
import com.usth.chat_app_api.user_info.UserInfo;
import com.usth.chat_app_api.utils.Helper;
import com.usth.chat_app_api.voip_call.IVoipCallService;
import com.usth.chat_app_api.voip_call.VoipCall;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class VideoStreamHandler extends TextWebSocketHandler {
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private IAwsSNSService awsSNSService;
    @Autowired
    private IICECandidateService iceCandidateService;
    @Autowired
    private IVoipCallService voipCallService;
    @Autowired
    private ICallTypeService callTypeService;
    @Autowired
    private ICallStatusService callStatusService;

    private Map<Long, WebSocketSession> sessionsMap = new HashMap<>();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        try {
            if (session.getUri() != null) {
                String queryParams = session.getUri().getQuery();
                Long userId = Long.parseLong(Helper.parseWsQuery(queryParams).toString());

                if (!sessionsMap.containsKey(userId)) {
                    sessionsMap.put(userId, session);
                    log.info("Video call socket connected - {}. Total session: {} ", userId, sessionsMap.keySet().size());
                } else {
                    log.error("Socket UserId " + userId + " already exist! --> Add again");
                    sessionsMap.remove(userId); // remove
                    sessionsMap.put(userId, session); // add again
                }
            } else {
                log.error("Video call socket connection fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        removeSession(session);
    }

    private void removeSession(WebSocketSession session) {
        try {
            if (session.getUri() != null) {
                String queryParams = session.getUri().getQuery();
                Long userId = Long.parseLong(Helper.parseWsQuery(queryParams).toString());

                if (sessionsMap.containsKey(userId)) {
                    sessionsMap.remove(userId);
                    log.info("Video call socket terminate user {} - Total session: {} ", userId, sessionsMap.keySet().size());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * If "action" is not null, we send answer to the caller
     * Else we push call notification to the receiver
     *
     * Video call type code: VIDEO
     */
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        ObjectMapper objectMapper = new ObjectMapper();

        Long userId;
        Long conversationId;
        String conversationName;
        String callType;
        final Long voipCallId;
        String callStatus;
        try {
            // get request
            String payload = message.getPayload();
            JsonNode jsonNode = objectMapper.readTree(payload);

            // handle end call
            if(jsonNode.get("callStatus") != null && jsonNode.get("callStatus").asText().equals(ApplicationConstant.END_CALL)){
                callStatus = jsonNode.get("callStatus") == null ? "": jsonNode.get("callStatus").asText();
                callType = jsonNode.get("callType") == null ? "": jsonNode.get("callType").asText();
                voipCallId = jsonNode.get("voipId") == null ? 0L: jsonNode.get("voipId").asLong();
                conversationId = jsonNode.get("conversationId") == null ? 0L: jsonNode.get("conversationId").asLong();

                handleEndCall(callStatus, callType, voipCallId, conversationId);

            }
            else if (jsonNode.get("userId") != null && jsonNode.get("conversationId") != null && jsonNode.get("conversationName") != null) {
                //
                userId = jsonNode.get("userId").asLong(0L);
                conversationId = jsonNode.get("conversationId").asLong(0L);
                conversationName = jsonNode.get("conversationName").asText("");
                callType = jsonNode.get("callType").asText("");
                voipCallId = jsonNode.get("voipId") != null ? jsonNode.get("voipId").asLong() : 0L;
                final String sdp = jsonNode.get("sdp").asText("");
                final String type = jsonNode.get("type").asText();

                // find callType
                CallType queriedCallType = callTypeService.findByCode(callType);

                // find conversation by id
                Conversation conversation = conversationService.findById(conversationId);
                if (conversation != null) {
                    // get all participants in conversation except current users
                    List<UserInfo> conversationParticipants = conversation
                            .getParticipants().stream()
                            .map(ConversationParticipant::getUser)
                            .toList();
                    Optional<UserInfo> currentUser = conversationParticipants.stream().filter(userInfo -> userInfo.getId().equals(userId)).findFirst();

                    // if type == offer -> push notification to the receiver
                    if (type.equals("offer") && currentUser.isPresent()) {
                        // save call to DB
                        VoipCall voipCall = new VoipCall();
                        voipCall.setCaller(currentUser.get());
                        voipCall.setTimeStart(new Timestamp(System.currentTimeMillis()));
                        voipCall.setConversationId(conversation);
                        voipCall.setCallTypeId(queriedCallType);
                        voipCall = voipCallService.save(voipCall);

                        // send VoipCall to the caller
                        ObjectNode node = objectMapper.createObjectNode();
                        node.put("voipId", voipCall.getId());
                        final String sendToCallerJson = objectMapper.writeValueAsString(node);
                        session.sendMessage(new TextMessage(sendToCallerJson));

                        for (UserInfo remoteUser : conversationParticipants) {
                            if (!remoteUser.getId().equals(userId) &&
                                    (remoteUser.getDeviceToken() != null && !remoteUser.getDeviceToken().isEmpty())) {
                                // push notification to remote user
                                // create body
                                ObjectNode objectNode = objectMapper.createObjectNode();
                                objectNode.put("sdp", sdp);
                                objectNode.put("voipId", voipCall.getId());
//                                objectNode.put("callerUserName", currentUser.get().getUsername());
                                if(currentUser.get().getProfilePicture() != null && !currentUser.get().getProfilePicture().trim().isEmpty()){
//                                    objectNode.put("callerAvtUrl", ApplicationConstant.S3_PATH + currentUser.get().getProfilePicture());
                                }
                                final String body = objectMapper.writeValueAsString(objectNode);
                                if (callType.equals(ApplicationConstant.VIDEO_CALL) || callType.equals(ApplicationConstant.AUDIO_CALL)) {
                                    // send notification
                                    CompletableFuture.runAsync(() ->
                                            awsSNSService.publishNotification(remoteUser.getDeviceToken(), callType,
                                                    body, conversationId));
                                }
                            }
                        }
                    }

                    // save ICE candidate of the caller to DB
                    if (jsonNode.get("isOffer") != null && jsonNode.get("isOffer").asBoolean() &&
                            jsonNode.get("voipId") != null ) {
                        // find VoipCall by id
                        Optional<VoipCall> voipCall = voipCallService.findById(jsonNode.get("voipId").asLong());
                        if(voipCall.isPresent()){
                            ICECandidate iceCandidate = new ICECandidate();
                            iceCandidate.setIceContent(payload);
                            iceCandidate.setVoipCallId(voipCall.get());

                            iceCandidateService.save(iceCandidate);
                            log.info("Save ICE candidate of user " + userId);
                        }
                    }

                    // send ICE Candidate to receiver
                    if (jsonNode.get("action") != null && jsonNode.get("action").asText().equals(ApplicationConstant.AGREE_CALL)) {
                        // find ICE candidate by VoipCall
                        List<ICECandidate> ICECandidateList = iceCandidateService.findAllByVoipCallId(voipCallId);

                        for (ICECandidate iceCandidate : ICECandidateList) {
                            session.sendMessage(new TextMessage(iceCandidate.getIceContent()));
                            log.info("Send ICE candidate to user {} - ICE: {} ", userId, iceCandidate.getIceContent());
                        }
                    }

                    if (type.equals("candidate")) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Method used to send answer
     * Params:
     *   conversationParticipants: list of participant in a conversation except the current user
     */
    private void sendAnswer(TextMessage data, List<UserInfo> conversationParticipants, String action, Long currentUserId) {
        log.info("On sendAnswer");
        try {
            for (UserInfo remoteUser : conversationParticipants) {
                Long remoteUserId = remoteUser.getId();

                if (sessionsMap.get(remoteUserId) != null && !remoteUserId.equals(currentUserId)) {
                    WebSocketSession session = sessionsMap.get(remoteUserId);
                    session.sendMessage(data);
                    log.info("Sent message to username: {} - userId: {}", remoteUser.getUsername(), remoteUserId);
                }
            }

            // close socket if the answer is DECLINE
            if (action != null && action.equals(ApplicationConstant.DECLINE_CALL)) {
                if (sessionsMap.get(currentUserId) != null) {
                    removeSession(sessionsMap.get(currentUserId));
                }
            }
        } catch (Exception e) {
            log.error("Cannot send data to remote user because " + e.getMessage());
        }
    }

    private void handleEndCall(String endCallCode, String callType, Long voipCallId, Long conversationId){
        try{
            // find conversation
            Conversation conversation = conversationService.findById(conversationId);
            if(conversation != null){
                List<UserInfo> participants = conversation.getParticipants()
                        .stream().map(ConversationParticipant::getUser).toList();
                for(UserInfo participant : participants){
                    WebSocketSession session = sessionsMap.get(participant.getId());
                    if(session != null){
                        session.close();
                        log.info("Session close for userId: {}", participant.getId());
                    }
                }
            }


            // end call time
            Timestamp endCallTime = new Timestamp(System.currentTimeMillis());

            // find CallStatus by code
            Optional<CallStatus> callStatus = callStatusService.findByCode(endCallCode);

            // find VoipCall
            Optional<VoipCall> voipCall = voipCallService.findById(voipCallId);
            if(callStatus.isPresent() && voipCall.isPresent() && conversation != null){
                // update VoipCall
                voipCall.get().setTimeEnd(endCallTime);
                voipCall.get().setCallStatusId(callStatus.get());
                voipCallService.save(voipCall.get());
            }
        } catch (Exception e){
            log.error("Cannot end VoipCall id {} because {}", voipCallId, e.getMessage());
        }
    }
}
