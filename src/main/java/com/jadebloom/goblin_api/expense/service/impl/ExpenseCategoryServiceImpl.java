package com.jadebloom.goblin_api.expense.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.expense.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.UpdateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryInUseException;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNameUnavailableException;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseCategoryException;
import com.jadebloom.goblin_api.expense.mapper.ExpenseCategoryMapper;
import com.jadebloom.goblin_api.expense.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.expense.repository.ExpenseRepository;
import com.jadebloom.goblin_api.expense.service.ExpenseCategoryService;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.repository.UserRepository;
import com.jadebloom.goblin_api.security.util.SecurityContextUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;
import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@Service
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

	private final ExpenseCategoryRepository expenseCategoryRepository;

	private final UserRepository userRepository;

	private final ExpenseRepository expenseRepository;

	private final ExpenseCategoryMapper mapper;

	public ExpenseCategoryServiceImpl(
			ExpenseCategoryRepository expenseCategoryRepository,
			UserRepository userRepository,
			ExpenseRepository expenseRepository,
			ExpenseCategoryMapper mapper) {
		this.expenseCategoryRepository = expenseCategoryRepository;

		this.userRepository = userRepository;

		this.expenseRepository = expenseRepository;

		this.mapper = mapper;
	}

	@Override
	public ExpenseCategoryDto create(CreateExpenseCategoryDto createDto)
			throws ForbiddenException,
			InvalidExpenseCategoryException,
			ExpenseCategoryNameUnavailableException {

		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new ForbiddenException());

		if (!GenericValidator.isValid(createDto)) {
			String message = GenericValidator.getValidationErrorMessage(createDto);

			throw new InvalidExpenseCategoryException(message);
		}

		String name = createDto.getName();
		if (expenseCategoryRepository.existsByName(name)) {
			String f = "Expense category with the name '%s' already exists";
			String errorMessage = String.format(f, name);

			throw new ExpenseCategoryNameUnavailableException(errorMessage);
		}

		ExpenseCategoryEntity created = mapper.map(createDto);
		created.setCreator(user);

		return mapper.map(expenseCategoryRepository.saveAndFlush(created));
	}

	@Override
	public Page<ExpenseCategoryDto> findAuthenticatedUserExpenseCategories(Pageable pageable)
			throws ForbiddenException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		Page<ExpenseCategoryEntity> page = expenseCategoryRepository.findAllByCreator_Id(
				userId, pageable);

		return page.map(mapper::map);
	}

	@Override
	public ExpenseCategoryDto findById(Long expenseCategoryId)
			throws ForbiddenException, ExpenseCategoryNotFoundException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		ExpenseCategoryEntity expenseCategory = expenseCategoryRepository
				.findById(expenseCategoryId)
				.orElseThrow(() -> {
					String f = "Expense category with the ID '%d' wasn't found";

					throw new ExpenseCategoryNotFoundException(String.format(f, expenseCategoryId));
				});

		if (expenseCategory.getCreator().getId() != userId) {
			throw new ForbiddenException();
		}

		return mapper.map(expenseCategory);
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

		ExpenseCategoryEntity expenseCategory = expenseCategoryRepository
				.findById(expenseCategoryId)
				.orElseThrow(() -> {
					String f = "Expense category with the ID '%d' doesn't exist";
					String errorMessage = String.format(f, expenseCategoryId);

					throw new ExpenseCategoryNotFoundException(errorMessage);
				});

		if (expenseCategory.getCreator().getId() != userId) {
			throw new ForbiddenException();
		}

		if (expenseCategoryRepository.existsByIdNotAndName(
				expenseCategoryId, updateDto.getName())) {
			String f = "Expense category with the name '%s' already exists";
			String errorMessage = String.format(f, updateDto.getName());

			throw new ExpenseCategoryNameUnavailableException(errorMessage);
		}

		expenseCategory.setName(updateDto.getName());
		expenseCategory.setDescription(updateDto.getDescription());
		expenseCategory.setHexColorCode(updateDto.getHexColorCode());

		return mapper.map(expenseCategoryRepository.saveAndFlush(expenseCategory));
	}

	@Override
	public void deleteById(Long expenseCategoryId)
			throws ForbiddenException,
			ExpenseCategoryNotFoundException,
			ExpenseCategoryInUseException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		ExpenseCategoryEntity expenseCategory = expenseCategoryRepository
				.findById(expenseCategoryId)
				.orElseThrow(() -> {
					String f = "Expense category with the ID '%d' doesn't exist";
					String errorMessage = String.format(f, expenseCategoryId);

					throw new ExpenseCategoryNotFoundException(errorMessage);
				});

		if (expenseCategory.getCreator().getId() != userId) {
			throw new ForbiddenException();
		}

		if (expenseRepository.existsByExpenseCategory_Id(expenseCategoryId)) {
			String f =
					"Cannot delete the expense category with the ID '%d': some amount of expenses reference it";
			String errorMessage = String.format(f, expenseCategoryId);

			throw new ExpenseCategoryInUseException(errorMessage);
		}

		expenseCategoryRepository.deleteById(expenseCategoryId);
	}

}
