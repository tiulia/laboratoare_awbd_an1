package com.awbd.lab6.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
