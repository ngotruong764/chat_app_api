package com.usth.chat_app_api.config_websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usth.chat_app_api.config_websocket.WebSocketSessionManager;
import com.usth.chat_app_api.message.MessageDTO;
import com.usth.chat_app_api.message.MessageService;
import com.usth.chat_app_api.user_info.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class MyHandler extends TextWebSocketHandler {

    @Autowired
    private MessageService messageService;

    @Autowired
    private WebSocketSessionManager sessionManager;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        MessageDTO messageDTO = objectMapper.readValue(message.getPayload(), MessageDTO.class);


        messageService.sendMessage(messageDTO.getUserId(), messageDTO.getConversationId(), messageDTO.getContent());


        List<UserInfo> participants = messageService.getParticipantsByConversationId(messageDTO.getConversationId());


        for (UserInfo participant : participants) {
            if (!participant.getId().equals(messageDTO.getUserId())) {
                WebSocketSession participantSession = sessionManager.getSession(participant.getId());

                if (participantSession != null && participantSession.isOpen()) {
                    participantSession.sendMessage(new TextMessage("New message: " + messageDTO.getContent()));
                }
            }
        }
        session.sendMessage(new TextMessage("Server confirm: " + messageDTO.getContent()));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        Long userId = getUserIdFromSession(session);
        sessionManager.addSession(userId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = getUserIdFromSession(session);
        sessionManager.removeSession(userId);
    }

    private Long getUserIdFromSession(WebSocketSession session) {
        // Giả sử bạn có userId trong URL của WebSocket hoặc qua một thuộc tính của session
        // Ví dụ: ws://localhost:8080/chat?userId=123
        String query = session.getUri().getQuery();
        return Long.parseLong(query.split("=")[1]);
    }
}
