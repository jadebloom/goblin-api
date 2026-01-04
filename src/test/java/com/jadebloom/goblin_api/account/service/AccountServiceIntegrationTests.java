package com.jadebloom.goblin_api.account.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;
import com.jadebloom.goblin_api.currency.repository.CurrencyRepository;
import com.jadebloom.goblin_api.expense.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense.entity.ExpenseEntity;
import com.jadebloom.goblin_api.expense.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.expense.repository.ExpenseRepository;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.repository.UserRepository;
import com.jadebloom.goblin_api.security.test.UserTestUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

@SpringBootTest
@Transactional
public class AccountServiceIntegrationTests {

	private final AccountService underTest;

	private final ExpenseCategoryRepository expenseCategoryRepository;

	private final CurrencyRepository currencyRepository;

	private final ExpenseRepository expenseRepository;

	private final UserTestUtils userTestUtils;

	private final UserRepository userRepository;

	private UserEntity user;

	@Autowired
	public AccountServiceIntegrationTests(
			AccountService underTest,
			UserTestUtils userTestUtils,
			ExpenseCategoryRepository expenseCategoryRepository,
			CurrencyRepository currencyRepository,
			ExpenseRepository expenseRepository,
			UserRepository userRepository) {
		this.underTest = underTest;

		this.expenseCategoryRepository = expenseCategoryRepository;

		this.currencyRepository = currencyRepository;

		this.expenseRepository = expenseRepository;

		this.userRepository = userRepository;

		this.userTestUtils = userTestUtils;
	}

	@BeforeEach
	public void createUserAndItsData() {
		user = userTestUtils.createUserWithPossiblyExistingRoles(
				"user@gmail.com",
				"123",
				Set.of("ROLE_USER"));

		ExpenseCategoryEntity toCreate1 = new ExpenseCategoryEntity("Daily", user);
		ExpenseCategoryEntity created1 = expenseCategoryRepository.saveAndFlush(toCreate1);

		CurrencyEntity toCreate2 = new CurrencyEntity("Tenge", user);
		CurrencyEntity created2 = currencyRepository.saveAndFlush(toCreate2);

		ExpenseEntity toCreate3 = new ExpenseEntity(
				"Uber Ride",
				10L,
				created1,
				created2,
				user);
		expenseRepository.save(toCreate3);
	}

	@Test
	@DisplayName("Verify that an existing account and its data can be deleted")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenExistingAuthenticatedUser_WhenDeletingAccount_ThenDelete() {
		underTest.deleteAccount();

		boolean isUserExists = userRepository.existsByEmail("user@gmail.com");

		List<ExpenseCategoryEntity> expenseCategories = expenseCategoryRepository.findAll();

		List<CurrencyEntity> currencies = currencyRepository.findAll();

		List<ExpenseEntity> expenses = expenseRepository.findAll();

		assertAll("Assert that an existing account and its data can be deleted",
				() -> assertFalse(isUserExists),
				() -> assertEquals(0, expenseCategories.size()),
				() -> assertEquals(0, currencies.size()),
				() -> assertEquals(0, expenses.size()));
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to delete the account without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenDeletingAccount_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class, () -> underTest.deleteAccount());
	}

}
