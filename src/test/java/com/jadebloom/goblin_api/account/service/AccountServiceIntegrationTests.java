package com.jadebloom.goblin_api.account.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;
import com.jadebloom.goblin_api.account.dto.UpdatePasswordDto;
import com.jadebloom.goblin_api.account.errors.InvalidPasswordException;
import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;
import com.jadebloom.goblin_api.currency.repository.CurrencyRepository;
import com.jadebloom.goblin_api.expense.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense.entity.ExpenseEntity;
import com.jadebloom.goblin_api.expense.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.expense.repository.ExpenseRepository;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.error.IncorrectPasswordException;
import com.jadebloom.goblin_api.security.repository.UserRepository;
import com.jadebloom.goblin_api.security.test.UserTestUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

@SpringBootTest
@Transactional
public class AccountServiceIntegrationTests {

	private final AccountService underTest;

	private final PasswordEncoder passwordEncoder;

	private final ExpenseCategoryRepository expenseCategoryRepository;

	private final CurrencyRepository currencyRepository;

	private final ExpenseRepository expenseRepository;

	private final UserRepository userRepository;

	private final UserTestUtils userTestUtils;

	private UserEntity user;

	@Autowired
	public AccountServiceIntegrationTests(
			AccountService underTest,
			PasswordEncoder passwordEncoder,
			ExpenseCategoryRepository expenseCategoryRepository,
			CurrencyRepository currencyRepository,
			ExpenseRepository expenseRepository,
			UserRepository userRepository,
			UserTestUtils userTestUtils) {
		this.underTest = underTest;

		this.passwordEncoder = passwordEncoder;

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
				passwordEncoder.encode("Qwerty123!"),
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
	@DisplayName("Verify that a user's password is updated given valid and correct passwords")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenValidCorrectPasswords_WhenUpdatingPassword_ThenUpdatePassword() {
		UpdatePasswordDto updateDto = new UpdatePasswordDto("Qwerty123!", "Qwerty321!");

		underTest.updatePassword(updateDto);

		assertTrue(passwordEncoder.matches(updateDto.getNewPassword(), user.getPassword()));
	}

	@Test
	@DisplayName("Throw InvalidPasswordException when trying to update using an invalid new password")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenInvalidNewPassword_WhenUpdatingPassword_ThenThrowInvalidPasswordException() {
		UpdatePasswordDto updateDto = new UpdatePasswordDto("Qwerty123!", "invalid!");

		assertThrowsExactly(InvalidPasswordException.class,
				() -> underTest.updatePassword(updateDto));
	}

	@Test
	@DisplayName("Throw IncorrectPasswordException when trying to update using an incorrect old password")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenIncorrectOldPassword_WhenUpdatingPassword_ThenThrowIncorrectPasswordException() {
		UpdatePasswordDto updateDto = new UpdatePasswordDto("Qwerty123!!", "Qwerty321!");

		assertThrowsExactly(IncorrectPasswordException.class,
				() -> underTest.updatePassword(updateDto));
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
