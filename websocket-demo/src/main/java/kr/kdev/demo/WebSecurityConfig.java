package kr.kdev.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
//                .csrf(csrf -> csrf.ignoringRequestMatchers("/ws/**", "/wss/**", "/ws-stomp/**")) // NOTE: Not working
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults())
                .securityContext(sc -> sc.requireExplicitSave(false)) // NOTE: https://docs.spring.io/spring-security/reference/servlet/authentication/persistence.html
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").authenticated()
                        .requestMatchers("/login", "/webjars/**").permitAll()
                        .requestMatchers("/ws/**", "/wss/**", "/ws-stomp/**").permitAll());
        return http.build();
    }
}