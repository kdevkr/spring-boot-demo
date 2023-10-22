package kr.kdev.demo;

import lombok.experimental.UtilityClass;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.session.web.socket.server.SessionRepositoryMessageInterceptor;

import java.util.Map;

@UtilityClass
public class StompUtil {
    public static String getHttpSessionId(Map<String, Object> sessionAttributes) {
        if (sessionAttributes == null || sessionAttributes.isEmpty()) {
            return null;
        }
        return SessionRepositoryMessageInterceptor.getSessionId(sessionAttributes);
    }

    public static String getWebSocketSessionId(MessageHeaders headers) {
        if (headers == null || headers.isEmpty()) {
            return null;
        }
        return SimpMessageHeaderAccessor.getSessionId(headers);
    }

    public static MessageHeaders createHeaders(String webSocketSessionId) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.MESSAGE);
        headerAccessor.setSessionId(webSocketSessionId);
        return headerAccessor.getMessageHeaders();
    }
}
