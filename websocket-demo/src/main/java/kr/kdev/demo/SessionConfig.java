package kr.kdev.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.MapSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class SessionConfig {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Bean
    public SessionRepository<MapSession> sessionRepository() {
        return new MapSessionRepository(sessions);
    }
}
