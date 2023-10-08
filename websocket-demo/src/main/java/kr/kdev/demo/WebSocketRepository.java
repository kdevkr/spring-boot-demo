package kr.kdev.demo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@AllArgsConstructor
@Slf4j
@Component
public class WebSocketRepository {

    private final Map<String, Set<WebSocketSession>> store = new ConcurrentHashMap<>();

    public void save(WebSocketSession session) {
        String sessionId = (String) session.getAttributes().get(HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME);

        if (sessionId != null) {
            Set<WebSocketSession> sessions = store.computeIfAbsent(sessionId, s -> new CopyOnWriteArraySet<>());
            sessions.add(session);

            store.put(sessionId, sessions);
        }
    }

    public void remove(WebSocketSession session) {
        String sessionId = (String) session.getAttributes().get(HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME);

        if (sessionId != null && store.containsKey(sessionId)) {
            Set<WebSocketSession> sessions = store.get(sessionId);
            sessions.remove(session);

            if (sessions.isEmpty()) {
                store.remove(sessionId);
            }
        }
    }

}
