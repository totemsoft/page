package com.totemsoft.page.exchangerates.v1.model;

public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ApiException(String error) {
        super(error);
    }

}
