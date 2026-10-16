package dev.demo.sisepp.shared.exception;

import dev.demo.sisepp.shared.filter.TraceIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsAppException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsAppException ex, HttpServletRequest req) {
        return error(HttpStatus.UNAUTHORIZED, "AUTH_BAD_CREDENTIALS", ex.getMessage(), req);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiError> handleLocked(AccountLockedException ex, HttpServletRequest req) {
        return error(HttpStatus.LOCKED, "AUTH_ACCOUNT_LOCKED", ex.getMessage(), req);
    }

    @ExceptionHandler(AccountInactiveException.class)
    public ResponseEntity<ApiError> handleInactive(AccountInactiveException ex, HttpServletRequest req) {
        return error(HttpStatus.FORBIDDEN, "AUTH_ACCOUNT_INACTIVE", ex.getMessage(), req);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateEmailException ex, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, "DUPLICATE_EMAIL", ex.getMessage(), req);
    }

    @ExceptionHandler(CannotInactivateDirectorException.class)
    public ResponseEntity<ApiError> handleCannotInactivateDirector(CannotInactivateDirectorException ex, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, "CANNOT_INACTIVATE_DIRECTOR", ex.getMessage(), req);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiError> handleInvalidToken(InvalidTokenException ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_TOKEN", ex.getMessage(), req);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex, HttpServletRequest req) {
        return error(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", ex.getMessage(), req);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return error(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", ex.getMessage(), req);
    }

    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity<ApiError> handleDuplicateName(DuplicateNameException ex, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, "DUPLICATE_NAME", ex.getMessage(), req);
    }

    @ExceptionHandler(InvalidCoordinatorRoleException.class)
    public ResponseEntity<ApiError> handleInvalidCoordinatorRole(InvalidCoordinatorRoleException ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_COORDINATOR_ROLE", ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(f -> f.getDefaultMessage())
            .collect(Collectors.joining(" "));
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, req);
    }

    @ExceptionHandler(WeightExceededException.class)
    public ResponseEntity<ApiError> handleWeightExceeded(WeightExceededException ex, HttpServletRequest req) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, "WEIGHT_EXCEEDED", ex.getMessage(), req);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT", ex.getMessage(), req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return error(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "No tiene permisos para ver esta sección", req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {
        log.error("Error inesperado en {} {}", req.getMethod(), req.getRequestURI(), ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Error interno del servidor", req);
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String code, String message, HttpServletRequest req) {
        ApiError body = new ApiError(status.value(), code, message, req.getRequestURI())
            .withTraceId(MDC.get(TraceIdFilter.TRACE_ID_KEY));
        return ResponseEntity.status(status).body(body);
    }
}
