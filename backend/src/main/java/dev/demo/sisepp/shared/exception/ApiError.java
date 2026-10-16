package dev.demo.sisepp.shared.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
    int status,
    String code,
    String message,
    String path,
    Instant timestamp,
    String traceId
) {
    public ApiError(int status, String code, String message, String path) {
        this(status, code, message, path, Instant.now(), null);
    }

    public ApiError withTraceId(String traceId) {
        return new ApiError(status, code, message, path, timestamp, traceId);
    }
}
