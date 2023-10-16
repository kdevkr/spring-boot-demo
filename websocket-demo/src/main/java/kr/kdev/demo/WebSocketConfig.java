package kr.kdev.demo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.session.MapSession;
import org.springframework.session.SessionRepository;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.session.web.socket.server.SessionRepositoryMessageInterceptor;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@AllArgsConstructor
@Slf4j
@EnableWebSocket
@Configuration
public class WebSocketConfig implements WebSocketConfigurer {

    private final SessionRepository<MapSession> sessionRepository;
    private final SessionRepositoryMessageInterceptor<MapSession> sessionRepositoryMessageInterceptor;
    private final WebSocketRepository webSocketSessionRepository;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(simpleHandler(), "/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new HttpSessionHandshakeInterceptor());

        // NOTE: WebSocket with SockJS
        registry.addHandler(simpleHandler(), "/wss")
                .setAllowedOriginPatterns("*")
                .addInterceptors(sessionRepositoryMessageInterceptor) // NOTE: insteadOf HttpSessionHandshakeInterceptor
                .withSockJS();
    }

    @Bean
    public WebSocketHandler simpleHandler() {
        return new TextWebSocketHandler() {
            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
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
                return SessionRepositoryMessageInterceptor.getSessionId(session.getAttributes());
            }
        };
    }

    @AllArgsConstructor
    @EnableWebSocketMessageBroker
    @Configuration
    public static class StompConfig extends AbstractSessionWebSocketMessageBrokerConfigurer<MapSession> {

        @Override
        protected void configureStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/ws-stomp")
                    .setAllowedOriginPatterns("*")
                    .withSockJS();
        }

        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
            registry.setApplicationDestinationPrefixes("/app")
                    .setPreservePublishOrder(true)
                    .enableSimpleBroker("/topic", "/queue"); // NOTE: /topic: Broadcast, /queue: Unicast
        }
    }

}
