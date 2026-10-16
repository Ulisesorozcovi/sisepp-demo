package dev.demo.sisepp.shared.exception;

public class WeightExceededException extends RuntimeException {
    public WeightExceededException(String message) {
        super(message);
    }
}
