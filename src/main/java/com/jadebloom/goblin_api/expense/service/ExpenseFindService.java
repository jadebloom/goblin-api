package com.jadebloom.goblin_api.expense.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

public interface ExpenseFindService {

	Page<ExpenseDto> findUserAuthenticatedExpenses(Pageable pageable) throws ForbiddenException;

	ExpenseDto findById(Long expenseId) throws ForbiddenException, ExpenseNotFoundException;

}
