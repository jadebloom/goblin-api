package com.jadebloom.goblin_api.account.service.impl;

import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.account.service.AccountService;
import com.jadebloom.goblin_api.currency.repository.CurrencyRepository;
import com.jadebloom.goblin_api.expense.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.expense.repository.ExpenseRepository;
import com.jadebloom.goblin_api.security.repository.UserRepository;
import com.jadebloom.goblin_api.security.util.SecurityContextUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

@Service
public class AccountServiceImpl implements AccountService {

	private final UserRepository userRepository;

	private final ExpenseRepository expenseRepository;

	private final ExpenseCategoryRepository expenseCategoryRepository;

	private final CurrencyRepository currencyRepository;

	public AccountServiceImpl(
			UserRepository userRepository,
			ExpenseRepository expenseRepository,
			ExpenseCategoryRepository expenseCategoryRepository,
			CurrencyRepository currencyRepository) {
		this.userRepository = userRepository;

		this.expenseRepository = expenseRepository;

		this.expenseCategoryRepository = expenseCategoryRepository;

		this.currencyRepository = currencyRepository;
	}

	@Override
	public void deleteAccount() throws ForbiddenException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		userRepository.findById(userId).orElseThrow(() -> new ForbiddenException());

		expenseRepository.deleteAllByCreator_Id(userId);

		expenseCategoryRepository.deleteAllByCreator_Id(userId);

		currencyRepository.deleteAllByCreator_Id(userId);

		userRepository.deleteById(userId);
	}

}
