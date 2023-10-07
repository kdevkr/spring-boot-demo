package kr.kdev.demo;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private int errorCode = 999;

    public ApiException() {
        super();
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

    public ApiException withErrorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }
}
