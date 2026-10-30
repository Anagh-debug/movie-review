package com.eventhosting.exception;

import com.eventhosting.dto.response.ResponseBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Failures come back in the same ResponseBean envelope as successes, so the
 * client only ever parses one shape.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseBean<Object>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseBean.error(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseBean<Object>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.badRequest()
                .body(ResponseBean.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseBean<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest()
                .body(ResponseBean.error(HttpStatus.BAD_REQUEST.value(),
                        "Request validation failed", fieldErrors));
    }

    /**
     * The request body is not valid JSON at all (a missing comma, an unclosed
     * quote, a stray bracket). Jackson cannot build the DTO, so bean validation
     * never runs - without this the request would fall through to the catch-all
     * and report 500, which wrongly blames the server for a client typo.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseBean<Object>> handleUnreadableBody(
            HttpMessageNotReadableException ex) {
        String detail = ex.getMostSpecificCause().getMessage();
        if (detail != null) {
            // Jackson appends a source reference that means nothing to the caller.
            detail = detail.split("\\n at \\[Source")[0].trim();
        }
        logger.warn("Malformed request body: {}", detail);
        return ResponseEntity.badRequest()
                .body(ResponseBean.error(HttpStatus.BAD_REQUEST.value(),
                        "Request body is not valid JSON: " + detail));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseBean<Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest()
                .body(ResponseBean.error(HttpStatus.BAD_REQUEST.value(),
                        "Invalid value for parameter '" + ex.getName() + "'"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseBean<Object>> handleUnexpected(Exception ex) {
        logger.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseBean.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred"));
    }
}
