package com.jadebloom.goblin_api.expense_category.service;

import com.jadebloom.goblin_api.expense_category.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense_category.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNameUnavailableException;
import com.jadebloom.goblin_api.expense_category.error.InvalidExpenseCategoryException;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

public interface ExpenseCategoryCreateService {

	ExpenseCategoryDto create(CreateExpenseCategoryDto createDto)
			throws ForbiddenException,
			InvalidExpenseCategoryException,
			ExpenseCategoryNameUnavailableException;

}
