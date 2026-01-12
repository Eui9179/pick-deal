package com.leui.storeservice.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Out of Stock")
public class OutOfStock extends RuntimeException {
    public OutOfStock(String message) {
        super(message);
    }
}
