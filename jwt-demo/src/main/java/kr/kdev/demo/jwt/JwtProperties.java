package kr.kdev.demo.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@AllArgsConstructor
@ConfigurationProperties(prefix = "spring.security.jwt")
@Getter
public class JwtProperties {
    private String privateKey;
    private String publicKey;
}
