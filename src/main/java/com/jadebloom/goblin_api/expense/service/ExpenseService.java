package com.jadebloom.goblin_api.expense.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.expense.dto.CreateExpenseDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseException;

public interface ExpenseService {

	ExpenseDto create(CreateExpenseDto createExpenseDto)
			throws ExpenseCategoryNotFoundException, CurrencyNotFoundException, InvalidExpenseException;

	Page<ExpenseDto> findAll(Pageable pageable);

	ExpenseDto findById(Long expenseId) throws ExpenseNotFoundException;

	boolean existsById(Long expenseId);

	ExpenseDto update(ExpenseDto expenseDto)
			throws ExpenseNotFoundException, InvalidExpenseException;

	void deleteAll();

	void deleteById(Long expenseId);

}
