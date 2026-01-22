package com.jadebloom.goblin_api.expense_category.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.expense.repository.ExpenseRepository;
import com.jadebloom.goblin_api.expense_category.dto.DeleteExpenseCategoriesDto;
import com.jadebloom.goblin_api.expense_category.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryInUseException;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense_category.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.expense_category.service.ExpenseCategoryDeleteService;
import com.jadebloom.goblin_api.security.util.SecurityContextUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

@Service
public class ExpenseCategoryDeleteServiceImpl implements ExpenseCategoryDeleteService {

	private final ExpenseCategoryRepository expenseCategoryRepo;

	private final ExpenseRepository expenseRepo;

	public ExpenseCategoryDeleteServiceImpl(ExpenseCategoryRepository expenseCategoryRepo,
			ExpenseRepository expenseRepo) {
		this.expenseCategoryRepo = expenseCategoryRepo;

		this.expenseRepo = expenseRepo;
	}

	@Override
	public void deleteAll() throws ForbiddenException, ExpenseCategoryInUseException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		List<ExpenseCategoryEntity> allCategories = expenseCategoryRepo.findAll();

		for (ExpenseCategoryEntity category : allCategories) {
			if (category.getCreator().getId() != userId) {
				throw new ForbiddenException();
			}
		}

		List<String> inUseCategoriesNames = new ArrayList<>();

		for (ExpenseCategoryEntity category : allCategories) {
			if (expenseRepo.existsByExpenseCategory_Id(category.getId())) {
				inUseCategoriesNames.add(category.getName());
			}
		}

		if (!inUseCategoriesNames.isEmpty()) {
			String s = "Failed to delete: categories with names ";

			for (int i = 0; i < inUseCategoriesNames.size(); i++) {
				String name = inUseCategoriesNames.get(i);

				if (i == inUseCategoriesNames.size() - 1) {
					s += "\"" + name + "\" ";
				} else {
					s += "\"" + name + "\", ";
				}
			}

			s += "cannot be deleted, because some amount of expenses reference them";

			throw new ExpenseCategoryInUseException(s);
		}

		expenseCategoryRepo.deleteAll();
	}

	@Override
	public void deleteSelectedById(DeleteExpenseCategoriesDto deleteDto)
			throws ForbiddenException, ExpenseCategoryInUseException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		List<Long> categoryIds = deleteDto.getExpenseCategoryIds();
		Set<Long> ids = categoryIds.stream().collect(Collectors.toSet());

		List<ExpenseCategoryEntity> categories = expenseCategoryRepo.findAllById(ids);

		for (ExpenseCategoryEntity category : categories) {
			if (category.getCreator().getId() != userId) {
				throw new ForbiddenException();
			}
		}

		List<String> inUseCategoriesNames = new ArrayList<>();

		for (ExpenseCategoryEntity category : categories) {
			if (expenseRepo.existsByExpenseCategory_Id(category.getId())) {
				inUseCategoriesNames.add(category.getName());
			}
		}

		if (!inUseCategoriesNames.isEmpty()) {
			String s = "Cannot delete provided expense categories: categories with names ";

			for (int i = 0; i < inUseCategoriesNames.size(); i++) {
				String name = inUseCategoriesNames.get(i);

				if (i == inUseCategoriesNames.size() - 1) {
					s += "\"" + name + "\" ";
				} else {
					s += "\"" + name + "\", ";
				}
			}

			s += "cannot be deleted, because some amount of expenses reference them";

			throw new ExpenseCategoryInUseException(s);
		}

		expenseCategoryRepo.deleteAllById(ids);
	}

	@Override
	public void deleteById(Long expenseCategoryId)
			throws ForbiddenException,
			ExpenseCategoryNotFoundException,
			ExpenseCategoryInUseException {
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

		if (expenseRepo.existsByExpenseCategory_Id(expenseCategoryId)) {
			String f = "Cannot delete the expense category with ID '%d': some amount of expenses reference it";
			String errorMessage = String.format(f, expenseCategoryId);

			throw new ExpenseCategoryInUseException(errorMessage);
		}

		expenseCategoryRepo.deleteById(expenseCategoryId);
	}

}
