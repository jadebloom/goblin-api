package com.jadebloom.goblin_api.currency.dto;

public class InvalidCurrencyException extends RuntimeException {

    public InvalidCurrencyException(String message) {
        super(message);
    }

}
