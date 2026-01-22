package com.jadebloom.goblin_api.expense_category.error;

public class ExpenseCategoryInUseException extends RuntimeException {

	public ExpenseCategoryInUseException(String message) {
		super(message);
	}

}
