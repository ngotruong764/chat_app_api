package com.usth.chat_app_api.config_websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class WebSocketSessionManager {

    private final Map<Long, List<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    public void addSession(Long userId, WebSocketSession session) {
        List<WebSocketSession> userSessions = sessions.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>());

        // check session if exist
        if (!userSessions.contains(session)) {
            userSessions.add(session);
            log.info("Added new session for userId: " + userId + ". Total sessions: " + userSessions.size());
        } else {
            log.info("Session already exists for userId: " + userId);
        }
    }


    public List<WebSocketSession> getSessions(Long userId) {
        return sessions.get(userId);
    }

    public void removeSession(Long userId, WebSocketSession session) {
        List<WebSocketSession> userSessions = sessions.get(userId);
        if (userSessions != null) {
            userSessions.remove(session);
            if (userSessions.isEmpty()) {
                sessions.remove(userId);
            }
        }
    }
    public Long getUserIdBySession(WebSocketSession session) {
        for (Map.Entry<Long, List<WebSocketSession>> entry : sessions.entrySet()) {
            if (entry.getValue().contains(session)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
