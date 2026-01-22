package com.jadebloom.goblin_api.expense_category.service.impl;

import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.expense_category.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense_category.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense_category.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNameUnavailableException;
import com.jadebloom.goblin_api.expense_category.error.InvalidExpenseCategoryException;
import com.jadebloom.goblin_api.expense_category.mapper.ExpenseCategoryMapper;
import com.jadebloom.goblin_api.expense_category.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.expense_category.service.ExpenseCategoryCreateService;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.repository.UserRepository;
import com.jadebloom.goblin_api.security.util.SecurityContextUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;
import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@Service
public class ExpenseCategoryCreateServiceImpl implements ExpenseCategoryCreateService {

	private final UserRepository userRepo;

	private final ExpenseCategoryRepository expenseCategoryRepo;

	private final ExpenseCategoryMapper mapper;

	public ExpenseCategoryCreateServiceImpl(UserRepository userRepo,
			ExpenseCategoryRepository expenseCategoryRepo,
			ExpenseCategoryMapper mapper) {
		this.userRepo = userRepo;

		this.expenseCategoryRepo = expenseCategoryRepo;

		this.mapper = mapper;
	}

	@Override
	public ExpenseCategoryDto create(CreateExpenseCategoryDto createDto)
			throws ForbiddenException,
			InvalidExpenseCategoryException,
			ExpenseCategoryNameUnavailableException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		UserEntity user = userRepo.findById(userId)
				.orElseThrow(() -> new ForbiddenException());

		if (!GenericValidator.isValid(createDto)) {
			String message = GenericValidator.getValidationErrorMessage(createDto);

			throw new InvalidExpenseCategoryException(message);
		}

		String name = createDto.getName();
		if (expenseCategoryRepo.existsByName(name)) {
			String f = "Expense category with name '%s' already exists";
			String errorMessage = String.format(f, name);

			throw new ExpenseCategoryNameUnavailableException(errorMessage);
		}

		ExpenseCategoryEntity created = mapper.map(createDto);
		created.setCreator(user);

		return mapper.map(expenseCategoryRepo.saveAndFlush(created));
	}

}
