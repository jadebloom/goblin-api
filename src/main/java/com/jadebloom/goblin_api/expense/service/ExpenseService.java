package com.jadebloom.goblin_api.expense.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.expense.dto.CreateExpenseDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.dto.UpdateExpenseDto;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseException;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

public interface ExpenseService {

	ExpenseDto create(CreateExpenseDto createDto)
			throws ForbiddenException,
			InvalidExpenseException,
			ExpenseCategoryNotFoundException,
			CurrencyNotFoundException;

	Page<ExpenseDto> findUserAuthenticatedExpenses(Pageable pageable) throws ForbiddenException;

	ExpenseDto findById(Long expenseId) throws ForbiddenException, ExpenseNotFoundException;

	ExpenseDto update(Long expenseId, UpdateExpenseDto updateDto)
			throws ForbiddenException,
			InvalidExpenseException,
			ExpenseNotFoundException,
			ExpenseCategoryNotFoundException,
			CurrencyNotFoundException;

	void deleteAll() throws ForbiddenException;

	void deleteAllExpensesByExpenseCategoryId(Long expenseCategoryId)
			throws ForbiddenException, ExpenseCategoryNotFoundException;

	void deleteAllExpensesByCurrencyId(Long currencyId)
			throws ForbiddenException, CurrencyNotFoundException;

	void deleteById(Long expenseId) throws ForbiddenException, ExpenseNotFoundException;

}
