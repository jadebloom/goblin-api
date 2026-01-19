package com.jadebloom.goblin_api.expense.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jadebloom.goblin_api.expense.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.UpdateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryInUseException;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNameUnavailableException;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseCategoryException;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

public interface ExpenseCategoryService {

	ExpenseCategoryDto create(CreateExpenseCategoryDto createDto)
			throws ForbiddenException,
			InvalidExpenseCategoryException,
			ExpenseCategoryNameUnavailableException;

	Page<ExpenseCategoryDto> findAuthenticatedUserExpenseCategories(Pageable pageable)
			throws ForbiddenException;

	ExpenseCategoryDto findById(Long expenseCategoryId)
			throws ForbiddenException, ExpenseCategoryNotFoundException;

	ExpenseCategoryDto update(Long expenseCategoryId, UpdateExpenseCategoryDto updateDto)
			throws ForbiddenException,
			InvalidExpenseCategoryException,
			ExpenseCategoryNotFoundException,
			ExpenseCategoryNameUnavailableException;

	void deleteById(Long expenseCategoryId)
			throws ForbiddenException,
			ExpenseCategoryNotFoundException,
			ExpenseCategoryInUseException;

}
