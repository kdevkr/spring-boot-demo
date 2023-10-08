package kr.kdev.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.Collections;
import java.util.List;
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

        String uri = "ws://localhost:%s/wss".formatted(port);
        CompletableFuture<WebSocketSession> execute = client.execute(new TextWebSocketHandler(), uri);
        WebSocketSession webSocketSession = execute.get();
        Assertions.assertTrue(webSocketSession.isOpen());
    }

}
