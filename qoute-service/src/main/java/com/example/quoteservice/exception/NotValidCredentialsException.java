package com.example.quoteservice.exception;

public class NotValidCredentialsException extends RuntimeException {
    public NotValidCredentialsException(String message) {
        super(message);
    }

}
