package kr.kdev.demo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ImmutableMessageChannelInterceptor;
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

    @AllArgsConstructor
    @EnableWebSocketMessageBroker
    @Configuration
    public static class StompConfig extends AbstractSessionWebSocketMessageBrokerConfigurer<MapSession> {

        private final SessionRepository<MapSession> sessionRepository;

        @Override
        protected void configureStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/ws/stomp")
                    .setAllowedOriginPatterns("*")
                    .withSockJS()
                    .setInterceptors(new HttpSessionHandshakeInterceptor());
        }

        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
            // NOTE: /topic: Broadcast, /queue: Unicast
            registry.enableSimpleBroker("/topic", "/queue");
            registry.setApplicationDestinationPrefixes("/app");
            registry.setPreservePublishOrder(true);
        }

        @Override
        public void configureClientInboundChannel(ChannelRegistration registration) {
            registration.interceptors(new ImmutableMessageChannelInterceptor());
            registration.interceptors(new SessionRepositoryMessageInterceptor<>(sessionRepository));
            registration.interceptors(new ChannelInterceptor() {
                @Override
                public Message<?> preSend(Message<?> message, MessageChannel channel) {
                    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                    StompCommand stompCommand = accessor.getCommand();

                    if (StompCommand.CONNECT.equals(stompCommand)) {
                        // TODO: Get authentication(principal) from spring-security
                        // accessor.setUser(user);
                    }

                    return ChannelInterceptor.super.preSend(message, channel);
                }
            });
        }
    }

}
