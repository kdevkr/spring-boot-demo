package kr.kdev.demo;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {
    @RequestMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        // NOTE: No need to call CsrfTokenRepository directly.
        return csrfToken;
    }
}