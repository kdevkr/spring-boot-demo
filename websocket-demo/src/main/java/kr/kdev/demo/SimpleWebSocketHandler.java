package kr.kdev.demo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.session.MapSession;
import org.springframework.session.SessionRepository;
import org.springframework.session.web.socket.server.SessionRepositoryMessageInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@AllArgsConstructor
@Slf4j
@Component
public class SimpleWebSocketHandler extends TextWebSocketHandler {

    private final SessionRepository<MapSession> sessionRepository;
    private final WebSocketRepository webSocketSessionRepository;

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        String sessionId = getSessionId(session);
        if (sessionId != null) {
            MapSession mapSession = sessionRepository.findById(sessionId);
            log.info("[handleMessage] {} - {}, {}", session, message.getPayload(), mapSession);
        } else {
            log.info("[handleMessage] {} - {}", session.getId(), message.getPayload());
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("[Connection Established] {}", session);

        if (session.isOpen()) {
            webSocketSessionRepository.save(session);
            session.sendMessage(new TextMessage("Hello, %s".formatted(getSessionId(session))));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        webSocketSessionRepository.remove(session);
        log.info("[Connection Closed] {} - {}", session, status);
    }

    private String getSessionId(WebSocketSession session) {
        return SessionRepositoryMessageInterceptor.getSessionId(session.getAttributes());
    }
}
