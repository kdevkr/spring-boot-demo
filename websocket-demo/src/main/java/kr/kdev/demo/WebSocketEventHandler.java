package kr.kdev.demo;

import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.session.web.socket.events.SessionConnectEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.security.Principal;
import java.util.Map;

@AllArgsConstructor
@Component
public class WebSocketEventHandler {

    private final WebSocketRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handle(SessionConnectEvent event) {
        repository.save(event.getWebSocketSession());
    }

    @EventListener
    public void handle(SessionConnectedEvent event) {
        MessageHeaders headers = event.getMessage().getHeaders();
        String wsSessionId = SimpMessageHeaderAccessor.getSessionId(headers);
        if (wsSessionId != null) {
            String username = "anonymous";
            Principal user = event.getUser();
            if (user != null) {
                username = user.getName();
            }
            String message = "Hi, %s".formatted(username);
            messagingTemplate.convertAndSendToUser(wsSessionId, "/queue/hello", Map.of("message", message));
        }
    }

    @EventListener
    public void handle(SessionDisconnectEvent event) {
        MessageHeaders headers = event.getMessage().getHeaders();
        Map<String, Object> sessionAttributes = SimpMessageHeaderAccessor.getSessionAttributes(headers);
        if (sessionAttributes != null) {
            String sessionId = repository.getSessionId(sessionAttributes);
            repository.remove(sessionId, event.getSessionId());
        }
    }

    @EventListener
    public void handle(SessionSubscribeEvent event) {
        // TODO: implementation
    }

    @EventListener
    public void handle(SessionUnsubscribeEvent event) {
        // TODO: implementation
    }
}
