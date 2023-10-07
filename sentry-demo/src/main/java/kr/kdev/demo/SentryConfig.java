package kr.kdev.demo;

import com.google.common.base.Throwables;
import io.sentry.Sentry;
import io.sentry.spring.jakarta.SentryTaskDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
@Configuration(proxyBeanMethods = false)
public class SentryConfig {

    private final boolean includeErrorDetails;

    public SentryConfig(Environment environment) {
        this.includeErrorDetails = environment.acceptsProfiles(Profiles.of("local"));
    }

    @Bean
    public SentryTaskDecorator sentryTaskDecorator() {
        return new SentryTaskDecorator();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ApiException.class)
    public ApiErrorResponse handleApiException(ApiException apiException) {
        Sentry.captureException(apiException);

        ApiErrorResponse.ApiErrorResponseBuilder builder = ApiErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(apiException.getMessage())
                .errorCode(apiException.getErrorCode())
                .timestamp(Instant.now());

        if (includeErrorDetails) {
            builder.exception(apiException.getClass())
                    .stackTrace(Throwables.getStackTraceAsString(apiException));
        }

        return builder.build();
    }
}
