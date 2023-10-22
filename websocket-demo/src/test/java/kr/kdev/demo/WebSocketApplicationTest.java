package kr.kdev.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * https://github.com/spring-projects/spring-framework/tree/main/spring-websocket/src/test/java/org/springframework/web/socket
 */

@DisplayName("웹소켓 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.main.banner-mode=off"
})
class WebSocketApplicationTest {

    @Autowired
    ServletWebServerApplicationContext applicationContext;

    @DisplayName("SockJS")
    @Test
    void TestSockJS() throws Exception {
        int port = applicationContext.getWebServer().getPort();

        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
        WebSocketTransport webSocketTransport = new WebSocketTransport(standardWebSocketClient);
        List<Transport> transports = Collections.singletonList(webSocketTransport);

        SockJsClient client = new SockJsClient(transports);
        client.start();

        String uri = "ws://localhost:%s/ws".formatted(port);
        CompletableFuture<WebSocketSession> execute = client.execute(new TextWebSocketHandler(), uri);
        WebSocketSession webSocketSession = execute.get();
        Assertions.assertTrue(webSocketSession.isOpen());
    }

    @DisplayName("Stomp with SockJS")
    @Test
    void TestStomp() throws Exception {
        int port = applicationContext.getWebServer().getPort();

        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
        WebSocketTransport webSocketTransport = new WebSocketTransport(standardWebSocketClient);
        List<Transport> transports = Collections.singletonList(webSocketTransport);

        WebSocketStompClient client = new WebSocketStompClient(new SockJsClient(transports));
        client.setMessageConverter(new MappingJackson2MessageConverter());
        client.start();

        StompSessionHandler handler = new StompSessionHandlerAdapter() {

            @Override
            public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
                session.subscribe("/topic/hello", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        String str = (String) payload;

                    }
                });
            }
        };

        String uri = "ws://localhost:%s/ws/stomp".formatted(port);
        CompletableFuture<StompSession> execute = client.connectAsync(uri, handler);
        StompSession stompSession = execute.get();

        Map<String, String> payload = new HashMap<>();
        payload.put("message", "Hello, %s".formatted(stompSession.getSessionId()));
        synchronized (stompSession) {
            stompSession.send("/app/hello", payload);
        }

        Thread.sleep(Duration.ofSeconds(15).toMillis());

        Assertions.assertTrue(stompSession.isConnected());
    }

}
