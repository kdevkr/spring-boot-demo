package kr.kdev.demo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.security.core.Authentication;
import org.springframework.session.web.socket.server.SessionRepositoryMessageInterceptor;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Slf4j
@RestController
public class StompController {

    private final SimpMessagingTemplate template;

    @SendTo("/topic/hello")
    @MessageMapping("/hello")
    public Map<String, String> hello(GenericMessage<String> message,
                                     @Header(name = "simpSessionId") String wsSessionId,
                                     @Header(name = "simpSessionAttributes") Map<String, Object> sessionAttributes,
                                     Principal principal) {
        String username = SessionRepositoryMessageInterceptor.getSessionId(sessionAttributes);
        if (principal instanceof Authentication) {
            username = principal.getName();
        }

        Map<String, String> payload = new HashMap<>();
        payload.put("message", "Hello, %s".formatted(username));
        payload.put("from", "StompController");

        // NOTE: similar @SendToUser
        template.convertAndSendToUser(wsSessionId, "/queue/hello", payload, message.getHeaders());
        return payload;
    }

    @MessageExceptionHandler
    @SendToUser(destinations = "/queue/errors", broadcast = false)
    public Map<String, String> handleException(Exception exception) {
        Map<String, String> error = new HashMap<>();
        error.put("exception", exception.getClass().getName());
        error.put("message", exception.getMessage());
        return error;
    }
}
