package com.jadebloom.goblin_api.expense.error;

public class InvalidExpenseException extends RuntimeException {

    public InvalidExpenseException(String message) {
        super(message);
    }

}
