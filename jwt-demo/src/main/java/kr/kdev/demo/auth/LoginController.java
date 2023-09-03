package kr.kdev.demo.auth;

import kr.kdev.demo.jwt.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@RestController
public class LoginController {

    private final JwtProvider jwtProvider;

    @PostMapping("login")
    public Map<String, Object> signIn() {
        // NOTE: This is a simple sample and implement user authentication management yourself.
        String username = UUID.randomUUID().toString();
        Date expiration = Date.from(Instant.ofEpochMilli(Instant.now().toEpochMilli() + Duration.ofHours(1).toMillis()));
        String jwt = jwtProvider.createJwt(username, expiration, "mambo", null);
        return Map.of("accessToken", jwt);
    }
}
