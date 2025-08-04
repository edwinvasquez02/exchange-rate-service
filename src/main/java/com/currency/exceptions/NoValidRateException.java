package com.currency.exceptions;

public class NoValidRateException extends RuntimeException {
    
    public NoValidRateException(String message) {
        super(message);
    }
    
}