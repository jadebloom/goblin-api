package com.jadebloom.goblin_api.expense_category.error;

public class ExpenseCategoryNotFoundException extends RuntimeException {

	public ExpenseCategoryNotFoundException(String message) {
		super(message);
	}

}
