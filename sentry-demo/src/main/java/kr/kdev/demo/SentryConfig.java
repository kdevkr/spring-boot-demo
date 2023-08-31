package kr.kdev.demo;

import io.sentry.Sentry;
import io.sentry.spring.jakarta.SentryTaskDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Configuration(proxyBeanMethods = false)
public class SentryConfig {
    @Bean
    public SentryTaskDecorator sentryTaskDecorator() {
        return new SentryTaskDecorator();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public Map<String, Object> handleThrowable(Throwable throwable) {
        Sentry.captureException(throwable);
        return Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.value(), "message", throwable.getMessage());
    }
}
