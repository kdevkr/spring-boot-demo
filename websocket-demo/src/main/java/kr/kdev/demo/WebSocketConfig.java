package kr.kdev.demo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.MapSession;
import org.springframework.session.SessionRepository;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@AllArgsConstructor
@Slf4j
@EnableWebSocket
@Configuration
public class WebSocketConfig implements WebSocketConfigurer {

    private final SessionRepository<MapSession> sessionRepository;
    private final WebSocketRepository webSocketSessionRepository;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(simpleHandler(), "/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new HttpSessionHandshakeInterceptor());

        // NOTE: WebSocket with SockJS
        registry.addHandler(simpleHandler(), "/wss")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .withSockJS();
    }

    @Bean
    public WebSocketHandler simpleHandler() {
        return new TextWebSocketHandler() {
            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                String sessionId = getSessionId(session);
                MapSession mapSession = sessionRepository.findById(sessionId);

                log.info("[handleMessage] {} - {}, {}", session, message.getPayload(), mapSession);
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
                return (String) session.getAttributes().get(HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME);
            }
        };
    }
}
