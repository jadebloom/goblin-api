package com.jadebloom.goblin_api.expense_category.service;

import com.jadebloom.goblin_api.expense_category.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense_category.dto.UpdateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNameUnavailableException;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense_category.error.InvalidExpenseCategoryException;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

public interface ExpenseCategoryUpdateService {

	ExpenseCategoryDto update(Long expenseCategoryId, UpdateExpenseCategoryDto updateDto)
			throws ForbiddenException,
			InvalidExpenseCategoryException,
			ExpenseCategoryNotFoundException,
			ExpenseCategoryNameUnavailableException;

}
