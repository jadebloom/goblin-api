package com.jadebloom.goblin_api.expense.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.expense.dto.DeleteExpensesDto;
import com.jadebloom.goblin_api.expense.entity.ExpenseEntity;
import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;
import com.jadebloom.goblin_api.expense.repository.ExpenseRepository;
import com.jadebloom.goblin_api.expense.service.ExpenseDeleteService;
import com.jadebloom.goblin_api.expense_category.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense_category.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.security.util.SecurityContextUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

@Service
public class ExpenseDeleteServiceImpl implements ExpenseDeleteService {

	private final ExpenseRepository expenseRepo;

	private final ExpenseCategoryRepository expenseCategoryRepo;

	public ExpenseDeleteServiceImpl(
			ExpenseRepository expenseRepo,
			ExpenseCategoryRepository expenseCategoryRepo) {
		this.expenseRepo = expenseRepo;

		this.expenseCategoryRepo = expenseCategoryRepo;
	}

	@Override
	public void deleteAll() throws ForbiddenException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		List<ExpenseEntity> allExpenses = expenseRepo.findAll();

		for (ExpenseEntity expense : allExpenses) {
			if (expense.getCreator().getId() != userId) {
				throw new ForbiddenException();
			}
		}

		expenseRepo.deleteAll();
	}

	@Override
	public void deleteSelectedById(DeleteExpensesDto deleteDto) throws ForbiddenException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		List<Long> expenseIds = deleteDto.getExpenseIds();

		Set<Long> ids = expenseIds.stream().collect(Collectors.toSet());

		List<ExpenseEntity> expenses = expenseRepo.findAllById(ids);

		for (ExpenseEntity expense : expenses) {
			if (expense.getCreator().getId() != userId) {
				throw new ForbiddenException();
			}
		}

		expenseRepo.deleteAllById(ids);
	}

	@Override
	public void deleteAllByExpenseCategoryId(Long expenseCategoryId)
			throws ForbiddenException, ExpenseCategoryNotFoundException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		ExpenseCategoryEntity expenseCategory = expenseCategoryRepo
				.findById(expenseCategoryId)
				.orElseThrow(() -> {
					String f = "Expense category with ID '%d' doesn't exist";
					String errorMessage = String.format(f, expenseCategoryId);

					throw new ExpenseCategoryNotFoundException(errorMessage);
				});

		if (expenseCategory.getCreator().getId() != userId) {
			throw new ForbiddenException();
		}

		expenseRepo.deleteAllByExpenseCategory_Id(expenseCategoryId);
	}

	@Override
	public void deleteById(Long expenseId) throws ForbiddenException, ExpenseNotFoundException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		ExpenseEntity expense = expenseRepo.findById(expenseId)
				.orElseThrow(() -> {
					String f = "Expense with ID '%d' wasn't found";

					throw new ExpenseNotFoundException(String.format(f, expenseId));
				});

		if (expense.getCreator().getId() != userId) {
			throw new ForbiddenException();
		}

		expenseRepo.deleteById(expenseId);
	}

}
