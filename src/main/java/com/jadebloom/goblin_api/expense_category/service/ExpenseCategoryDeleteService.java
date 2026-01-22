package com.jadebloom.goblin_api.expense_category.service;

import com.jadebloom.goblin_api.expense_category.dto.DeleteExpenseCategoriesDto;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryInUseException;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

public interface ExpenseCategoryDeleteService {

	void deleteAll() throws ForbiddenException, ExpenseCategoryInUseException;

	void deleteSelectedById(DeleteExpenseCategoriesDto deleteDto)
			throws ForbiddenException, ExpenseCategoryInUseException;

	void deleteById(Long expenseCategoryId)
			throws ForbiddenException,
			ExpenseCategoryNotFoundException,
			ExpenseCategoryInUseException;

}
