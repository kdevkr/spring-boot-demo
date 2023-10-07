package kr.kdev.demo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@Accessors
@Getter
public class ApiErrorResponse {
    private Class<?> exception;
    private String message;
    private HttpStatus status;
    private int errorCode;
    private Instant timestamp;
    private String stackTrace;
}
