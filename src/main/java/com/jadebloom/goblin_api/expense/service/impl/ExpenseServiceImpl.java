package com.jadebloom.goblin_api.expense.service.impl;

import java.util.Optional;

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
			throws InvalidExpenseException,
			ForbiddenException,
			ExpenseCategoryNotFoundException,
			CurrencyNotFoundException {
		if (!GenericValidator.isValid(createDto)) {
			String message = GenericValidator.getValidationErrorMessage(createDto);

			throw new InvalidExpenseException(message);
		}

		Optional<String> optCreatorEmail = SecurityContextUtils.getAuthenticatedUserEmail();
		if (optCreatorEmail.isEmpty()) {
			throw new ForbiddenException();
		}
		String creatorEmail = optCreatorEmail.get();

		Optional<UserEntity> optCreator = userRepository.findByEmail(creatorEmail);
		if (optCreator.isEmpty()) {
			throw new ForbiddenException();
		}
		UserEntity creator = optCreator.get();

		Long expenseCategoryId = createDto.getExpenseCategoryId();
		if (!expenseCategoryRepository.existsById(expenseCategoryId)) {
			String f = "Expense category with the ID '%d' wasn't found";

			throw new ExpenseCategoryNotFoundException(String.format(f, expenseCategoryId));
		}

		Long currencyId = createDto.getCurrencyId();
		if (!currencyRepository.existsById(currencyId)) {
			String f = "Currency with the ID '%d' wasn't found";

			throw new CurrencyNotFoundException(String.format(f, currencyId));
		}

		ExpenseEntity expense = mapper.map(createDto);
		expense.setCreator(creator);

		return mapper.map(expenseRepository.saveAndFlush(expense));
	}

	@Override
	public Page<ExpenseDto> findUserAuthenticatedExpenses(Pageable pageable) throws ForbiddenException {
		Optional<String> optCreatorEmail = SecurityContextUtils.getAuthenticatedUserEmail();
		if (optCreatorEmail.isEmpty()) {
			throw new ForbiddenException();
		}
		String creatorEmail = optCreatorEmail.get();

		Page<ExpenseEntity> page = expenseRepository.findAllByCreator_Email(creatorEmail, pageable);

		return page.map(mapper::map);
	}

	@Override
	public ExpenseDto findById(Long expenseId) throws ForbiddenException, ExpenseNotFoundException {
		Optional<String> optCreatorEmail = SecurityContextUtils.getAuthenticatedUserEmail();
		if (optCreatorEmail.isEmpty()) {
			throw new ForbiddenException();
		}
		String creatorEmail = optCreatorEmail.get();

		Optional<ExpenseEntity> optExpense = expenseRepository.findById(expenseId);
		if (optExpense.isEmpty()) {
			String f = "Expense with the ID '%d' wasn't found";

			throw new ExpenseNotFoundException(String.format(f, expenseId));
		}

		ExpenseEntity expense = optExpense.get();
		if (!expense.getCreator().getEmail().equals(creatorEmail)) {
			throw new ForbiddenException();
		}

		return mapper.map(expense);
	}

	@Override
	public ExpenseDto update(UpdateExpenseDto updateDto)
			throws InvalidExpenseException,
			ForbiddenException,
			ExpenseNotFoundException,
			ExpenseCategoryNotFoundException,
			CurrencyNotFoundException {
		if (!GenericValidator.isValid(updateDto)) {
			String message = GenericValidator.getValidationErrorMessage(updateDto);

			throw new InvalidExpenseException(message);
		}

		Optional<String> optCreatorEmail = SecurityContextUtils.getAuthenticatedUserEmail();
		if (optCreatorEmail.isEmpty()) {
			throw new ForbiddenException();
		}
		String creatorEmail = optCreatorEmail.get();

		Optional<ExpenseEntity> optExpense = expenseRepository.findById(updateDto.getId());
		if (optExpense.isEmpty()) {
			String f = "Expense with the ID '%d' wasn't found";

			throw new ExpenseNotFoundException(String.format(f, updateDto.getId()));
		}

		ExpenseEntity expense = optExpense.get();
		if (!expense.getCreator().getEmail().equals(creatorEmail)) {
			throw new ForbiddenException();
		}

		Long expenseCategoryId = updateDto.getExpenseCategoryId();
		Optional<ExpenseCategoryEntity> optExpenseCategory = expenseCategoryRepository.findById(
				expenseCategoryId);
		if (optExpenseCategory.isEmpty()) {
			String f = "Expense category with the ID '%d' wasn't found";
			String message = String.format(f, expenseCategoryId);

			throw new ExpenseCategoryNotFoundException(message);
		}
		ExpenseCategoryEntity expenseCategory = optExpenseCategory.get();

		Long currencyId = updateDto.getCurrencyId();
		Optional<CurrencyEntity> optCurrency = currencyRepository.findById(currencyId);
		if (optCurrency.isEmpty()) {
			String f = "Currency with the ID '%d' wasn't found";
			String message = String.format(f, currencyId);

			throw new CurrencyNotFoundException(message);
		}
		CurrencyEntity currency = optCurrency.get();

		expense.setName(updateDto.getName());
		expense.setDescription(updateDto.getDescription());
		expense.setAmount(updateDto.getAmount());
		expense.setLabels(updateDto.getLabels());
		expense.setExpenseCategory(expenseCategory);
		expense.setCurrency(currency);

		return mapper.map(expenseRepository.saveAndFlush(expense));
	}

	@Override
	public void deleteById(Long expenseId) throws ForbiddenException {
		Optional<String> optUserEmail = SecurityContextUtils.getAuthenticatedUserEmail();
		if (optUserEmail.isEmpty()) {
			throw new ForbiddenException();
		}

		expenseRepository.deleteById(expenseId);
	}

}
