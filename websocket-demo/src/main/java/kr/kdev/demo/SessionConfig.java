package kr.kdev.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.MapSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EnableSpringHttpSession // NOTE: Use 'SESSION' cookie insteadOf JSESSIONID.
@Configuration
public class SessionConfig {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Bean
    public SessionRepository<MapSession> sessionRepository() {
        return new MapSessionRepository(sessions);
    }
}
