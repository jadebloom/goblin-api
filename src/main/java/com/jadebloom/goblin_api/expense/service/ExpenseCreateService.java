package com.jadebloom.goblin_api.expense.service;

import com.jadebloom.goblin_api.expense.dto.CreateExpenseDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseException;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

public interface ExpenseCreateService {

	ExpenseDto create(CreateExpenseDto createDto)
			throws ForbiddenException,
			InvalidExpenseException,
			ExpenseCategoryNotFoundException;

}
