package kr.kdev.demo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.session.web.socket.events.SessionConnectEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
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
        log.info("[WebSocket Connected] {}", event);
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
        String destination = event.getMessage().getHeaders().get("simpDestination", String.class);
        MessageHeaders headers = event.getMessage().getHeaders();
        String wsSessionId = SimpMessageHeaderAccessor.getSessionId(headers);
        if (wsSessionId != null && destination != null && destination.startsWith("/user/queue/hello")) {
            String username = null;
            Principal principal = event.getUser();
            if (principal != null) {
                username = principal.getName();
            }
            Map<String, String> payload = new HashMap<>();
            payload.put("message", "Hi, %s".formatted(username));
            payload.put("from", "WebSocketEventHandler");

            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
            headerAccessor.setSessionId(wsSessionId);
            headerAccessor.setLeaveMutable(true);

            String user = username != null ? username : wsSessionId;
            messagingTemplate.convertAndSendToUser(user, "/queue/hello", payload, headerAccessor.getMessageHeaders());
        }
    }
}
