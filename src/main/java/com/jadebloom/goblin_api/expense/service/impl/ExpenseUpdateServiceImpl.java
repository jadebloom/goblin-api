package com.jadebloom.goblin_api.expense.service.impl;

import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.dto.UpdateExpenseDto;
import com.jadebloom.goblin_api.expense.entity.ExpenseEntity;
import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseException;
import com.jadebloom.goblin_api.expense.mapper.ExpenseMapper;
import com.jadebloom.goblin_api.expense.repository.ExpenseRepository;
import com.jadebloom.goblin_api.expense.service.ExpenseUpdateService;
import com.jadebloom.goblin_api.expense_category.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense_category.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.security.util.SecurityContextUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;
import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@Service
public class ExpenseUpdateServiceImpl implements ExpenseUpdateService {

	private final ExpenseRepository expenseRepo;

	private final ExpenseCategoryRepository expenseCategoryRepo;

	private final ExpenseMapper mapper;

	public ExpenseUpdateServiceImpl(ExpenseRepository expenseRepo,
			ExpenseCategoryRepository expenseCategoryRepo,
			ExpenseMapper mapper) {
		this.expenseRepo = expenseRepo;

		this.expenseCategoryRepo = expenseCategoryRepo;

		this.mapper = mapper;
	}

	@Override
	public ExpenseDto update(Long expenseId, UpdateExpenseDto updateDto) throws ForbiddenException,
			InvalidExpenseException, ExpenseNotFoundException, ExpenseCategoryNotFoundException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		if (!GenericValidator.isValid(updateDto)) {
			String message = GenericValidator.getValidationErrorMessage(updateDto);

			throw new InvalidExpenseException(message);
		}

		ExpenseEntity expense = expenseRepo.findById(expenseId)
				.orElseThrow(() -> {
					String f = "Expense with ID '%d' wasn't found";

					throw new ExpenseNotFoundException(String.format(f, expenseId));
				});

		if (expense.getCreator().getId() != userId) {
			throw new ForbiddenException();
		}

		Long expenseCategoryId = updateDto.getExpenseCategoryId();
		ExpenseCategoryEntity expenseCategory = expenseCategoryRepo
				.findById(expenseCategoryId)
				.orElseThrow(() -> {
					String f = "Expense category with ID '%d' wasn't found";
					String errorMessage = String.format(f, expenseCategoryId);

					throw new ExpenseCategoryNotFoundException(errorMessage);
				});

		expense.setName(updateDto.getName());
		expense.setDescription(updateDto.getDescription());
		expense.setAmount(updateDto.getAmount());
		expense.setCurrencyCode(updateDto.getCurrencyCode());
		expense.setLabels(updateDto.getLabels());
		expense.setExpenseCategory(expenseCategory);

		return mapper.map(expenseRepo.saveAndFlush(expense));
	}

}
