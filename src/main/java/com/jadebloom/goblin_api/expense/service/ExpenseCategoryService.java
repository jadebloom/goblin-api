package com.jadebloom.goblin_api.expense.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jadebloom.goblin_api.expense.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNameUnavailableException;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;

public interface ExpenseCategoryService {

	ExpenseCategoryDto create(CreateExpenseCategoryDto createDto)
			throws ExpenseCategoryNameUnavailableException;

	Page<ExpenseCategoryDto> findAll(Pageable pageable);

	ExpenseCategoryDto findById(Long expenseCategoryId) throws ExpenseCategoryNotFoundException;

	boolean existsById(Long expenseCategoryId);

	ExpenseCategoryDto update(ExpenseCategoryDto dto)
			throws ExpenseCategoryNotFoundException,
			ExpenseCategoryNameUnavailableException;

	void deleteById(Long expenseCategoryId);

}
