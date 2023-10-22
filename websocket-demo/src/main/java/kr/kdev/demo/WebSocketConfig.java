package kr.kdev.demo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.session.MapSession;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.session.web.socket.server.SessionRepositoryMessageInterceptor;
import org.springframework.web.socket.config.annotation.*;

@AllArgsConstructor
@Slf4j
@EnableWebSocket
@Configuration
public class WebSocketConfig implements WebSocketConfigurer {

    private static final String NONE_CDN_SOCKJS = "/webjars/sockjs-client/sockjs.min.js";
    private final SessionRepositoryMessageInterceptor<MapSession> sessionRepositoryMessageInterceptor;
    private final SimpleWebSocketHandler simpleWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(simpleWebSocketHandler, "/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(sessionRepositoryMessageInterceptor);

        // NOTE: WebSocket with SockJS
        registry.addHandler(simpleWebSocketHandler, "/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(sessionRepositoryMessageInterceptor) // NOTE: insteadOf HttpSessionHandshakeInterceptor
                .withSockJS()
                .setClientLibraryUrl(NONE_CDN_SOCKJS);
    }

    @AllArgsConstructor
    @EnableWebSocketMessageBroker
    @Configuration
    public static class StompConfig extends AbstractSessionWebSocketMessageBrokerConfigurer<MapSession> {

        @Override
        protected void configureStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/wss")
                    .setAllowedOriginPatterns("*")
                    .withSockJS()
                    .setClientLibraryUrl(NONE_CDN_SOCKJS);
        }

        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
            registry.setApplicationDestinationPrefixes("/app")
                    .setPreservePublishOrder(true)
                    .enableSimpleBroker("/topic", "/queue"); // NOTE: /topic: Broadcast, /queue: Unicast
        }
    }

}
