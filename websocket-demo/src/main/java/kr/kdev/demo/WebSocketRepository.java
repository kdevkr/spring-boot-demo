package kr.kdev.demo;

import org.springframework.session.web.socket.server.SessionRepositoryMessageInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class WebSocketRepository {

    private final Map<String, Set<WebSocketSession>> store = new ConcurrentHashMap<>();

    public Set<WebSocketSession> findById(String sessionId) {
        return store.get(sessionId);
    }

    public void save(WebSocketSession session) {
        String sessionId = getSessionId(session);

        if (sessionId != null) {
            Set<WebSocketSession> sessions = store.computeIfAbsent(sessionId, s -> new CopyOnWriteArraySet<>());
            sessions.add(session);

            store.put(sessionId, sessions);
        }
    }

    public void remove(WebSocketSession session) {
        String sessionId = getSessionId(session);

        if (sessionId != null && store.containsKey(sessionId)) {
            Set<WebSocketSession> sessions = store.get(sessionId);
            sessions.remove(session);

            if (sessions.isEmpty()) {
                store.remove(sessionId);
            }
        }
    }

    public void remove(String sessionId, String wsSessionId) {
        if (sessionId != null && store.containsKey(sessionId)) {
            Set<WebSocketSession> sessions = store.get(sessionId);
            Optional<WebSocketSession> sessionOptional = Optional.empty();
            for (WebSocketSession session : sessions) {
                if (session.getId().equals(wsSessionId)) {
                    sessionOptional = Optional.of(session);
                    break;
                }
            }

            sessionOptional.ifPresent(this::remove);
        }
    }

    public String getSessionId(WebSocketSession session) {
        return getSessionId(session.getAttributes());
    }

    public String getSessionId(Map<String, Object> sessionAttributes) {
        String sessionId = (String) sessionAttributes.get(HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME);
        if (sessionId != null) {
            sessionId = SessionRepositoryMessageInterceptor.getSessionId(sessionAttributes);
        }
        return sessionId;
    }
}
