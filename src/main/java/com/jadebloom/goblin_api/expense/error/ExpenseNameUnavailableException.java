package com.jadebloom.goblin_api.expense.error;

public class ExpenseNameUnavailableException extends RuntimeException {

    public ExpenseNameUnavailableException(String message) {
        super(message);
    }

}
