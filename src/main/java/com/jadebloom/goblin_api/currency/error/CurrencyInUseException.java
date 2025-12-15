package com.jadebloom.goblin_api.currency.error;

public class CurrencyInUseException extends RuntimeException {

    public CurrencyInUseException(String message) {
        super(message);
    }

}
