package com.jadebloom.goblin_api.expense.service;

import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.dto.UpdateExpenseDto;
import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseException;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

public interface ExpenseUpdateService {

	ExpenseDto update(Long expenseId, UpdateExpenseDto updateDto)
			throws ForbiddenException,
			InvalidExpenseException,
			ExpenseNotFoundException,
			ExpenseCategoryNotFoundException;

}
