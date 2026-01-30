package com.totemsoft.page.config;

import org.apache.tomcat.util.http.InvalidParameterException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.totemsoft.page.service.EntityNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public final ResponseEntity<ErrorResponse> defaultErrorHandler(Throwable ex, WebRequest request) throws Exception {
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return error(status, ex, "Unexpected Server Error");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(EntityNotFoundException ex, WebRequest request) throws Exception {
        final HttpStatus status = HttpStatus.NOT_FOUND;
        return error(status, ex, null);
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<ErrorResponse> handle(InvalidParameterException ex, WebRequest request) throws Exception {
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        return error(status, ex, null);
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, Throwable ex, String message) {
        final var error = new ErrorResponse(
                status,
                message == null ? ex.getMessage() : (message + ": " + ex.getMessage()));
        return new ResponseEntity<>(error, status);
    }

}
