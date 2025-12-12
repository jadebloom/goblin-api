package com.jadebloom.goblin_api.expense.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jadebloom.goblin_api.expense.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseCategoryException;

public interface ExpenseCategoryService {

	ExpenseCategoryDto create(CreateExpenseCategoryDto createExpenseCategoryDto)
			throws InvalidExpenseCategoryException;

	Page<ExpenseCategoryDto> findAll(Pageable pageable);

	ExpenseCategoryDto findById(Long expenseCategoryId) throws ExpenseCategoryNotFoundException;

	boolean existsById(Long expenseCategoryId);

	ExpenseCategoryDto update(ExpenseCategoryDto expenseCategoryDto)
			throws ExpenseCategoryNotFoundException, InvalidExpenseCategoryException;

	void deleteAll();

	void deleteById(Long expenseCategoryId);

}
