package com.jadebloom.goblin_api.expense.service;

import com.jadebloom.goblin_api.expense.dto.DeleteExpensesDto;
import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

public interface ExpenseDeleteService {

	void deleteAll() throws ForbiddenException;

	void deleteSelectedById(DeleteExpensesDto deleteDto) throws ForbiddenException;

	void deleteAllByExpenseCategoryId(Long expenseCategoryId)
			throws ForbiddenException, ExpenseCategoryNotFoundException;

	void deleteById(Long expenseId) throws ForbiddenException, ExpenseNotFoundException;

}
