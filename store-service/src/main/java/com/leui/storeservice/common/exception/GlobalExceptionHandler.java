package com.leui.storeservice.common.exception;

import exception.OutOfStock;
import exception.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .code("Entity not found.")
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(OutOfStock.class)
    public ResponseEntity<ErrorResponse> handlerOutOfStock(OutOfStock e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .code("Out of Stock.")
                        .message(e.getMessage())
                        .build());
    }
}
