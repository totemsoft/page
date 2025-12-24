package com.totemsoft.page.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> defaultErrorHandler(Exception ex, WebRequest request) throws Exception {
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return error(status, ex, "Unexpected Server Error");
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, Exception ex, String message) {
        final ErrorResponse error = new ErrorResponse(
                status.value(),
                message != null ? message : ex.getMessage());
        return new ResponseEntity<>(error, status);
    }

}
