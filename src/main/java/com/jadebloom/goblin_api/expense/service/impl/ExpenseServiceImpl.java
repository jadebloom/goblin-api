package com.jadebloom.goblin_api.expense.service.impl;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;
import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.currency.repository.CurrencyRepository;
import com.jadebloom.goblin_api.expense.dto.CreateExpenseDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.dto.UpdateExpenseDto;
import com.jadebloom.goblin_api.expense.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense.entity.ExpenseEntity;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseException;
import com.jadebloom.goblin_api.expense.mapper.ExpenseMapper;
import com.jadebloom.goblin_api.expense.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.expense.repository.ExpenseRepository;
import com.jadebloom.goblin_api.expense.service.ExpenseService;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.repository.UserRepository;
import com.jadebloom.goblin_api.security.util.SecurityContextUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;
import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@Service
public class ExpenseServiceImpl implements ExpenseService {

	private final ExpenseRepository expenseRepository;

	private final UserRepository userRepository;

	private final ExpenseCategoryRepository expenseCategoryRepository;

	private final CurrencyRepository currencyRepository;

	private final ExpenseMapper mapper;

	public ExpenseServiceImpl(
			ExpenseRepository expenseRepository,
			UserRepository userRepository,
			ExpenseCategoryRepository expenseCategoryRepository,
			CurrencyRepository currencyRepository,
			ExpenseMapper mapper) {
		this.expenseRepository = expenseRepository;

		this.userRepository = userRepository;

		this.expenseCategoryRepository = expenseCategoryRepository;

		this.currencyRepository = currencyRepository;

		this.mapper = mapper;
	}

	@Override
	public ExpenseDto create(CreateExpenseDto createDto)
			throws ForbiddenException,
			InvalidExpenseException,
			ExpenseCategoryNotFoundException,
			CurrencyNotFoundException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new ForbiddenException());

		if (!GenericValidator.isValid(createDto)) {
			String message = GenericValidator.getValidationErrorMessage(createDto);

			throw new InvalidExpenseException(message);
		}

		Long expenseCategoryId = createDto.getExpenseCategoryId();
		ExpenseCategoryEntity expenseCategory = expenseCategoryRepository
				.findById(expenseCategoryId)
				.orElseThrow(() -> {
					String f = "Expense category with the ID '%d' wasn't found";
					String errorMessage = String.format(f, expenseCategoryId);

					throw new ExpenseCategoryNotFoundException(errorMessage);
				});

		Long currencyId = createDto.getCurrencyId();
		CurrencyEntity currency = currencyRepository
				.findById(currencyId)
				.orElseThrow(() -> {
					String f = "Currency with the ID '%d' wasn't found";
					String errorMessage = String.format(f, currencyId);

					throw new CurrencyNotFoundException(errorMessage);
				});

		ExpenseEntity expense = mapper.map(createDto);
		expense.setExpenseCategory(expenseCategory);
		expense.setCurrency(currency);
		expense.setCreator(user);

		return mapper.map(expenseRepository.saveAndFlush(expense));
	}

	@Override
	public Page<ExpenseDto> findUserAuthenticatedExpenses(Pageable pageable)
			throws ForbiddenException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		Page<ExpenseEntity> page = expenseRepository.findAllByCreator_Id(userId, pageable);

		return page.map(mapper::map);
	}

	@Override
	public ExpenseDto findById(Long expenseId) throws ForbiddenException, ExpenseNotFoundException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		ExpenseEntity expense = expenseRepository.findById(expenseId)
				.orElseThrow(() -> {
					String f = "Expense with the ID '%d' wasn't found";

					throw new ExpenseNotFoundException(String.format(f, expenseId));
				});

		if (expense.getCreator().getId() != userId) {
			throw new ForbiddenException();
		}

		return mapper.map(expense);
	}

	@Override
	public ExpenseDto update(Long expenseId, UpdateExpenseDto updateDto)
			throws ForbiddenException,
			InvalidExpenseException,
			ExpenseNotFoundException,
			ExpenseCategoryNotFoundException,
			CurrencyNotFoundException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		if (!GenericValidator.isValid(updateDto)) {
			String message = GenericValidator.getValidationErrorMessage(updateDto);

			throw new InvalidExpenseException(message);
		}

		ExpenseEntity expense = expenseRepository.findById(expenseId)
				.orElseThrow(() -> {
					String f = "Expense with the ID '%d' wasn't found";

					throw new ExpenseNotFoundException(String.format(f, expenseId));
				});

		if (expense.getCreator().getId() != userId) {
			throw new ForbiddenException();
		}

		Long expenseCategoryId = updateDto.getExpenseCategoryId();
		ExpenseCategoryEntity expenseCategory = expenseCategoryRepository
				.findById(expenseCategoryId)
				.orElseThrow(() -> {
					String f = "Expense category with the ID '%d' wasn't found";
					String errorMessage = String.format(f, expenseCategoryId);

					throw new ExpenseCategoryNotFoundException(errorMessage);
				});

		Long currencyId = updateDto.getCurrencyId();
		CurrencyEntity currency = currencyRepository
				.findById(currencyId)
				.orElseThrow(() -> {
					String f = "Currency with the ID '%d' wasn't found";
					String errorMessage = String.format(f, currencyId);

					throw new CurrencyNotFoundException(errorMessage);
				});

		expense.setName(updateDto.getName());
		expense.setDescription(updateDto.getDescription());
		expense.setAmount(updateDto.getAmount());
		expense.setLabels(updateDto.getLabels());
		expense.setExpenseCategory(expenseCategory);
		expense.setCurrency(currency);

		return mapper.map(expenseRepository.saveAndFlush(expense));
	}

	@Override
	public void deleteAll() throws ForbiddenException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		List<ExpenseEntity> allExpenses = expenseRepository.findAll();

		for (ExpenseEntity expense : allExpenses) {
			if (expense.getCreator().getId() != userId) {
				throw new ForbiddenException();
			}
		}

		expenseRepository.deleteAll();
	}

	@Override
	public void deleteAllExpensesByExpenseCategoryId(Long expenseCategoryId)
			throws ForbiddenException, ExpenseCategoryNotFoundException {
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

		expenseRepository.deleteAllByExpenseCategory_Id(expenseCategoryId);
	}

	@Override
	public void deleteAllExpensesByCurrencyId(Long currencyId)
			throws ForbiddenException, CurrencyNotFoundException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		CurrencyEntity currency = currencyRepository
				.findById(currencyId)
				.orElseThrow(() -> {
					String f = "Currency with the ID '%d' doesn't exist";
					String errorMessage = String.format(f, currencyId);

					throw new CurrencyNotFoundException(errorMessage);
				});

		if (currency.getCreator().getId() != userId) {
			throw new ForbiddenException();
		}

		expenseRepository.deleteAllByCurrency_Id(currencyId);
	}

	@Override
	public void deleteById(Long expenseId) throws ForbiddenException, ExpenseNotFoundException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		ExpenseEntity expense = expenseRepository.findById(expenseId)
				.orElseThrow(() -> {
					String f = "Expense with the ID '%d' wasn't found";

					throw new ExpenseNotFoundException(String.format(f, expenseId));
				});

		if (expense.getCreator().getId() != userId) {
			throw new ForbiddenException();
		}

		expenseRepository.deleteById(expenseId);
	}

}
