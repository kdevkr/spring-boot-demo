package kr.kdev.demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class CsrfController {

    private final CsrfTokenRepository csrfTokenRepository;

    @RequestMapping("/csrf")
    public CsrfToken csrf(HttpServletRequest request, HttpServletResponse response) {
        return csrfTokenRepository.loadDeferredToken(request, response).get();
    }
}