package dev.demo.sisepp.shared.exception;

public class BadCredentialsAppException extends RuntimeException {
    public BadCredentialsAppException(String message) {
        super(message);
    }
}
