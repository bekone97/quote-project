package com.example.quoteservice.exception;

import static com.example.quoteservice.util.ConstantUtil.Exception.NOT_UNIQUE_RESOURCE_PATTERN;

public class NotUniqueResourceException extends RuntimeException {

    public NotUniqueResourceException(String message) {
        super(message);
    }

    public NotUniqueResourceException(Class<?> resourceType, String fieldName, Object fieldValue) {
        super(String.format(NOT_UNIQUE_RESOURCE_PATTERN,
                resourceType.getSimpleName(), fieldName, fieldValue));
    }
}
