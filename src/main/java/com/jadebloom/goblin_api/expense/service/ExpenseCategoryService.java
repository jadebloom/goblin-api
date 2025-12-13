package com.jadebloom.goblin_api.expense.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jadebloom.goblin_api.expense.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNameUnavailableException;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseCategoryException;

public interface ExpenseCategoryService {

	ExpenseCategoryDto create(CreateExpenseCategoryDto createExpenseCategoryDto)
			throws InvalidExpenseCategoryException, ExpenseCategoryNameUnavailableException;

	Page<ExpenseCategoryDto> findAll(Pageable pageable);

	ExpenseCategoryDto findById(Long expenseCategoryId) throws ExpenseCategoryNotFoundException;

	boolean existsById(Long expenseCategoryId);

	boolean existsByName(String expenseCategoryName);

	boolean existsByIdNotAndName(Long expenseCategoryId, String expenseCategoryName);

	ExpenseCategoryDto update(ExpenseCategoryDto expenseCategoryDto)
			throws ExpenseCategoryNotFoundException,
			InvalidExpenseCategoryException,
			ExpenseCategoryNameUnavailableException;

	void deleteAll();

	void deleteById(Long expenseCategoryId);

}
