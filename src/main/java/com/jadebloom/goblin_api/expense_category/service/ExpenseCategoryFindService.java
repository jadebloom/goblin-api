package com.jadebloom.goblin_api.expense_category.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jadebloom.goblin_api.expense_category.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

public interface ExpenseCategoryFindService {

	Page<ExpenseCategoryDto> findAuthenticatedUserExpenseCategories(Pageable pageable)
			throws ForbiddenException;

	ExpenseCategoryDto findById(Long expenseCategoryId)
			throws ForbiddenException, ExpenseCategoryNotFoundException;

}
