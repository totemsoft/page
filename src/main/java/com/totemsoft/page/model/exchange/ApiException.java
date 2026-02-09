package com.totemsoft.page.model.exchange;

public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ApiException(String error) {
        super(error);
    }

}
