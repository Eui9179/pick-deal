package com.leui.userservice.domain.auth.exception;

public class NotAuthorizationException extends RuntimeException {
    public NotAuthorizationException(String message, String subject) {

        super(message);
    }
}
