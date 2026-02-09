package com.totemsoft.page.config;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.totemsoft.page.service.EntityNotFoundException;

import lombok.extern.log4j.Log4j2;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public final ResponseEntity<Object> defaultErrorHandler(Throwable ex, WebRequest request) {
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return errorAndLog(status, ex, "Unexpected Server Error");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handle(EntityNotFoundException ex, WebRequest request) {
        final HttpStatus status = HttpStatus.NOT_FOUND;
        return errorAndLog(status, ex, null);
    }

    //@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @Override
    protected @Nullable ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return error(status, ex, null);
        //return super.handleHttpRequestMethodNotSupported(ex, headers, status, request);
    }

    private ResponseEntity<Object> error(HttpStatusCode status, Throwable ex, String message) {
        final var error = new ErrorResponse(
                status,
                message == null ? ex.getMessage() : (message + ": " + ex.getMessage()));
        return new ResponseEntity<>(error, status);
    }

    private ResponseEntity<Object> errorAndLog(HttpStatusCode status, Throwable ex, String message) {
        log.error(message, ex);
        return error(status, ex, message);
    }

}
