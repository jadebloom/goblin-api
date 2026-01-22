package com.jadebloom.goblin_api.expense.service.impl;

import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.expense.dto.CreateExpenseDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.entity.ExpenseEntity;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseException;
import com.jadebloom.goblin_api.expense.mapper.ExpenseMapper;
import com.jadebloom.goblin_api.expense.repository.ExpenseRepository;
import com.jadebloom.goblin_api.expense.service.ExpenseCreateService;
import com.jadebloom.goblin_api.expense_category.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense_category.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.repository.UserRepository;
import com.jadebloom.goblin_api.security.util.SecurityContextUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;
import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@Service
public class ExpenseCreateServiceImpl implements ExpenseCreateService {

	private final ExpenseRepository expenseRepo;

	private final ExpenseCategoryRepository expenseCategoryRepo;

	private final UserRepository userRepo;

	private final ExpenseMapper mapper;

	public ExpenseCreateServiceImpl(ExpenseRepository expenseRepo,
			ExpenseCategoryRepository expenseCategoryRepo,
			UserRepository userRepo,
			ExpenseMapper mapper) {
		this.expenseRepo = expenseRepo;

		this.expenseCategoryRepo = expenseCategoryRepo;

		this.userRepo = userRepo;

		this.mapper = mapper;
	}

	@Override
	public ExpenseDto create(CreateExpenseDto createDto)
			throws ForbiddenException, InvalidExpenseException, ExpenseCategoryNotFoundException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		UserEntity user = userRepo.findById(userId)
				.orElseThrow(() -> new ForbiddenException());

		if (!GenericValidator.isValid(createDto)) {
			String message = GenericValidator.getValidationErrorMessage(createDto);

			throw new InvalidExpenseException(message);
		}

		Long expenseCategoryId = createDto.getExpenseCategoryId();
		ExpenseCategoryEntity expenseCategory = expenseCategoryRepo
				.findById(expenseCategoryId)
				.orElseThrow(() -> {
					String f = "Expense category with ID '%d' wasn't found";
					String errorMessage = String.format(f, expenseCategoryId);

					throw new ExpenseCategoryNotFoundException(errorMessage);
				});

		ExpenseEntity expense = mapper.map(createDto);
		expense.setExpenseCategory(expenseCategory);
		expense.setCreator(user);

		return mapper.map(expenseRepo.saveAndFlush(expense));
	}

}
