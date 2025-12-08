package com.jadebloom.goblin_api.expense.error;

public class ExpenseCategoryNotFoundException extends RuntimeException {

    public ExpenseCategoryNotFoundException(String message) {
        super(message);
    }

}
