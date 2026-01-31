package com.totemsoft.page.service;

import java.io.Serializable;

public class EntityNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public <T extends Serializable> EntityNotFoundException(T entityId, Class<?> entityClass) {
        super("Could not find " + entityClass.getSimpleName() + " #" + entityId);
    }

}
