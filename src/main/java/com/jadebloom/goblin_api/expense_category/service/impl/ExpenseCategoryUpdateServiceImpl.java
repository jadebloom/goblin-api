package com.jadebloom.goblin_api.expense_category.service.impl;

import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.expense_category.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense_category.dto.UpdateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense_category.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNameUnavailableException;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense_category.error.InvalidExpenseCategoryException;
import com.jadebloom.goblin_api.expense_category.mapper.ExpenseCategoryMapper;
import com.jadebloom.goblin_api.expense_category.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.expense_category.service.ExpenseCategoryUpdateService;
import com.jadebloom.goblin_api.security.util.SecurityContextUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;
import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@Service
public class ExpenseCategoryUpdateServiceImpl implements ExpenseCategoryUpdateService {

	private final ExpenseCategoryRepository expenseCategoryRepo;

	private final ExpenseCategoryMapper mapper;

	public ExpenseCategoryUpdateServiceImpl(ExpenseCategoryRepository expenseCategoryRepo,
			ExpenseCategoryMapper mapper) {
		this.expenseCategoryRepo = expenseCategoryRepo;

		this.mapper = mapper;
	}

	@Override
	public ExpenseCategoryDto update(Long expenseCategoryId, UpdateExpenseCategoryDto updateDto)
			throws ForbiddenException,
			InvalidExpenseCategoryException,
			ExpenseCategoryNameUnavailableException,
			ExpenseCategoryNotFoundException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		if (!GenericValidator.isValid(updateDto)) {
			String message = GenericValidator.getValidationErrorMessage(updateDto);

			throw new InvalidExpenseCategoryException(message);
		}

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

		if (expenseCategoryRepo.existsByIdNotAndName(
				expenseCategoryId, updateDto.getName())) {
			String f = "Expense category with name '%s' already exists";
			String errorMessage = String.format(f, updateDto.getName());

			throw new ExpenseCategoryNameUnavailableException(errorMessage);
		}

		expenseCategory.setName(updateDto.getName());
		expenseCategory.setDescription(updateDto.getDescription());
		expenseCategory.setHexColorCode(updateDto.getHexColorCode());

		return mapper.map(expenseCategoryRepo.saveAndFlush(expenseCategory));
	}

}
