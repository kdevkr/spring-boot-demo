package kr.kdev.demo.jwt;

import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtParser jwtParser;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        jwtParser = Jwts.parserBuilder()
                .setSigningKey(jwtProvider.getPublicKey())
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authorization) && authorization.startsWith(BEARER_PREFIX)) {
            String bearerToken = authorization.substring(BEARER_PREFIX.length());
            Jws<Claims> claimsJws = verifyToken(bearerToken);
            if (claimsJws != null) {
                Authentication authentication = createAuthentication(bearerToken, claimsJws);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private Authentication createAuthentication(String bearerToken, Jws<Claims> claimsJws) {
        Claims claims = claimsJws.getBody();

        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        if (claims.containsKey("authorities")) {
            authorities = Arrays.stream(claims.get("authorities").toString().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        }

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, bearerToken, authorities);
    }

    private Jws<Claims> verifyToken(String token) {
        if (StringUtils.hasText(token)) {
            try {
                return jwtParser.parseClaimsJws(token);
            } catch (JwtException e) {
                log.error("{}", e.getMessage());
            }
        }
        return null;
    }
}
