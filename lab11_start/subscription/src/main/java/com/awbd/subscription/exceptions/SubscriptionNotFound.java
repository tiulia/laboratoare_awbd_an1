package com.awbd.subscription.exceptions;

//@ResponseStatus(HttpStatus.NOT_FOUND)
public class SubscriptionNotFound extends RuntimeException {
    public SubscriptionNotFound(String message) {
        super(message);
    }
}
