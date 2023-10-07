package kr.kdev.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class SentryApplication {
    public static void main(String[] args) {
        SpringApplication.run(SentryApplication.class, args);
    }

    @GetMapping("/")
    public String index() {
        return "ok";
    }

    @GetMapping("/e")
    public String e() {
        throw new ApiException("Hello").withErrorCode(1);
    }
}
