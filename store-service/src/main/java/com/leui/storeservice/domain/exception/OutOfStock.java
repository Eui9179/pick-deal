package com.leui.storeservice.domain.exception;

public class OutOfStock extends RuntimeException {
    public OutOfStock(String message) {
        super(message);
    }
}
