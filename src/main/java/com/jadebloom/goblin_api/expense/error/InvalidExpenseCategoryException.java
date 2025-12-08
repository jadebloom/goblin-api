package com.jadebloom.goblin_api.expense.error;

public class InvalidExpenseCategoryException extends RuntimeException {

    public InvalidExpenseCategoryException(String message) {
        super(message);
    }

}
