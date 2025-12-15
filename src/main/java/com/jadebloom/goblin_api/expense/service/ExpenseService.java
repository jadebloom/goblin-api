package com.jadebloom.goblin_api.expense.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.expense.dto.CreateExpenseDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.error.ExpenseNameUnavailableException;
import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;

public interface ExpenseService {

	ExpenseDto create(CreateExpenseDto createDto)
			throws ExpenseNameUnavailableException,
			ExpenseCategoryNotFoundException,
			CurrencyNotFoundException;

	Page<ExpenseDto> findAll(Pageable pageable);

	ExpenseDto findById(Long expenseId) throws ExpenseNotFoundException;

	ExpenseDto update(ExpenseDto dto)
			throws ExpenseNotFoundException,
			ExpenseNameUnavailableException,
			ExpenseCategoryNotFoundException,
			CurrencyNotFoundException;

	void deleteById(Long expenseId);

}
