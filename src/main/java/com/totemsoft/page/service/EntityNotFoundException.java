package com.totemsoft.page.service;

public class EntityNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EntityNotFoundException(Long entityId, Class<?> entityClass) {
        super("Could not find " + entityClass.getSimpleName() + " #" + entityId);
    }

}
