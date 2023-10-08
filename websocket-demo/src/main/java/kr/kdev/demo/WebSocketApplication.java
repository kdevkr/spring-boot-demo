package kr.kdev.demo;

import jakarta.servlet.http.HttpSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@SpringBootApplication
public class WebSocketApplication extends AbstractHttpSessionApplicationInitializer {
    public static void main(String[] args) {
        SpringApplication.run(WebSocketApplication.class, args);
    }

    @GetMapping("/")
    public String index(HttpSession session) {
        return "index";
    }
}
