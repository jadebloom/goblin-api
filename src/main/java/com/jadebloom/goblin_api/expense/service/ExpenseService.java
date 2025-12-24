package com.jadebloom.goblin_api.expense.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.expense.dto.CreateExpenseDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.dto.UpdateExpenseDto;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.error.ExpenseNameUnavailableException;
import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

public interface ExpenseService {

	ExpenseDto create(CreateExpenseDto createDto)
			throws ForbiddenException,
			ExpenseNameUnavailableException,
			ExpenseCategoryNotFoundException,
			CurrencyNotFoundException;

	Page<ExpenseDto> findUserAuthenticatedExpenses(Pageable pageable) throws ForbiddenException;

	ExpenseDto findById(Long expenseId) throws ForbiddenException, ExpenseNotFoundException;

	ExpenseDto update(UpdateExpenseDto updateDto)
			throws ForbiddenException,
			ExpenseNotFoundException,
			ExpenseNameUnavailableException,
			ExpenseCategoryNotFoundException,
			CurrencyNotFoundException;

	void deleteById(Long expenseId) throws ForbiddenException;

}
