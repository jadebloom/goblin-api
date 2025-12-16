package com.jadebloom.goblin_api.expense.error;

public class ExpenseCategoryInUseException extends RuntimeException {

    public ExpenseCategoryInUseException(String message) {
        super(message);
    }

}
