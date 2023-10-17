package kr.kdev.demo;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final ServerProperties serverProperties;

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        Session.Cookie cookie = serverProperties.getServlet().getSession().getCookie();
        CookieCsrfTokenRepository tokenRepository = new CookieCsrfTokenRepository();
        tokenRepository.setCookieCustomizer(c -> c.secure(cookie.getSecure())
                .path(cookie.getPath())
                .httpOnly(cookie.getHttpOnly())
                .sameSite(cookie.getSameSite().attributeValue())
                .maxAge(Duration.ofMinutes(30)));
        return tokenRepository;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults())
                .rememberMe(Customizer.withDefaults())
                .securityContext(sc -> sc.requireExplicitSave(false)) // NOTE: https://docs.spring.io/spring-security/reference/servlet/authentication/persistence.html
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").authenticated()
                        .requestMatchers("/login", "/webjars/**", "/csrf").permitAll()
                        .requestMatchers("/ws/**", "/wss/**", "/ws-stomp/**").permitAll());

        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .sessionRegistry(new SessionRegistryImpl()));

        // NOTE: Protection Against Exploits
        http
                .csrf(csrf -> csrf.csrfTokenRepository(csrfTokenRepository())
                        .ignoringRequestMatchers("/ws/**", "/wss/**", "/ws-stomp/**")) // NOTE: SockJS /info HTTP Endpoint
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        return http.build();
    }
}