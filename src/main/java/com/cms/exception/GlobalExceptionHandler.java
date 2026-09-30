package com.cms.exception;

import com.cms.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<ApiError> e(HttpStatus s, String m, HttpServletRequest r) {
        return ResponseEntity.status(s).body(new ApiError(LocalDateTime.now(), s.value(), s.getReasonPhrase(), m, r.getRequestURI()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiError> notFound(ResourceNotFoundException x, HttpServletRequest r) {
        return e(HttpStatus.NOT_FOUND, x.getMessage(), r);
    }

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ApiError> bad(BusinessException x, HttpServletRequest r) {
        return e(HttpStatus.BAD_REQUEST, x.getMessage(), r);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException x, HttpServletRequest r) {
        String m = x.getBindingResult().getFieldErrors().stream().map(f -> f.getField() + ": " + f.getDefaultMessage()).collect(Collectors.joining("; "));
        return e(HttpStatus.BAD_REQUEST, m, r);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiError> integrity(DataIntegrityViolationException x, HttpServletRequest r) {
        return e(HttpStatus.CONFLICT, "Database constraint violation", r);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> general(Exception x, HttpServletRequest r) {
        return e(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", r);
    }
}
