package com.jadebloom.goblin_api.expense.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.expense.dto.CreateExpenseDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.error.ExpenseNameUnavailableException;
import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseException;

public interface ExpenseService {

	ExpenseDto create(CreateExpenseDto createExpenseDto)
			throws InvalidExpenseException,
			ExpenseNameUnavailableException,
			ExpenseCategoryNotFoundException,
			CurrencyNotFoundException;

	Page<ExpenseDto> findAll(Pageable pageable);

	ExpenseDto findById(Long expenseId) throws ExpenseNotFoundException;

	boolean existsByCurrencyId(Long currencyId);

	ExpenseDto update(ExpenseDto expenseDto)
			throws InvalidExpenseException,
			ExpenseNotFoundException,
			ExpenseNameUnavailableException,
			ExpenseCategoryNotFoundException,
			CurrencyNotFoundException;

	void deleteAll();

	void deleteById(Long expenseId);

}
