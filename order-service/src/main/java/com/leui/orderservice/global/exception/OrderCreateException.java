package com.leui.orderservice.global.exception;

public class OrderCreateException extends RuntimeException {
    public OrderCreateException(String message) {
        super(message);
    }
}
