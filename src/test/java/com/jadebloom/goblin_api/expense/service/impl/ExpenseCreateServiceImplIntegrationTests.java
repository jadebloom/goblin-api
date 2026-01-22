package com.jadebloom.goblin_api.expense.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import com.jadebloom.goblin_api.expense.dto.CreateExpenseDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseException;
import com.jadebloom.goblin_api.expense_category.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense_category.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.test.UserTestUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

@SpringBootTest
@Transactional
public class ExpenseCreateServiceImplIntegrationTests {

	private final ExpenseCreateServiceImpl underTest;

	private final ExpenseCategoryRepository expenseCategoryRepo;

	private final UserTestUtils userTestUtils;

	private ExpenseCategoryEntity expenseCategory;

	private UserEntity user;

	@Autowired
	public ExpenseCreateServiceImplIntegrationTests(ExpenseCreateServiceImpl underTest,
			ExpenseCategoryRepository expenseCategoryRepo,
			UserTestUtils userTestUtils) {
		this.underTest = underTest;

		this.expenseCategoryRepo = expenseCategoryRepo;

		this.userTestUtils = userTestUtils;
	}

	@BeforeEach
	public void createDependencies() {
		user = userTestUtils.createUserWithPossiblyExistingRoles(
				"user@gmail.com",
				"123",
				Set.of("ROLE_USER"));

		ExpenseCategoryEntity toCreate1 = new ExpenseCategoryEntity("Daily", user);
		expenseCategory = expenseCategoryRepo.saveAndFlush(toCreate1);
	}

	@Test
	@DisplayName("Return an expense when creating it with valid all fields")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenValidExpenseWithAllFields_WhenCreating_ThenReturnExpense() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				1000L,
				"USD",
				expenseCategory.getId());

		createDto.setDescription("Magnificent description");

		String label1 = "Label1", label2 = "Label2";
		createDto.setLabels(List.of(label1, label2));

		ExpenseDto created = underTest.create(createDto);

		assertAll("Assert that an expense with valid all fields can be created and returned",
				() -> assertNotNull(created.getId()),
				() -> assertEquals(createDto.getName(), created.getName()),
				() -> assertEquals(createDto.getDescription(), created.getDescription()),
				() -> assertEquals(createDto.getAmount(), created.getAmount()),
				() -> assertEquals(createDto.getCurrencyCode(), created.getCurrencyCode()),
				() -> assertEquals(2, created.getLabels().size()),
				() -> assertTrue(created.getLabels().contains(label1)),
				() -> assertTrue(created.getLabels().contains(label2)),
				() -> assertNotNull(created.getCreatedAt()),
				() -> assertEquals(expenseCategory.getId(), created.getExpenseCategoryId()));
	}

	@Test
	@DisplayName("Return an expense when creating it with only required fields")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenValidExpenseWithOnlyRequiredFields_WhenCreating_ThenReturnExpense() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				1000L,
				"USD",
				expenseCategory.getId());

		ExpenseDto created = underTest.create(createDto);

		assertAll("Assert that an expense with valid all fields can be created and returned",
				() -> assertNotNull(created.getId()),
				() -> assertEquals(createDto.getName(), created.getName()),
				() -> assertNull(created.getDescription()),
				() -> assertEquals(createDto.getAmount(), created.getAmount()),
				() -> assertEquals(createDto.getCurrencyCode(), created.getCurrencyCode()),
				() -> assertNull(created.getLabels()),
				() -> assertNotNull(created.getCreatedAt()),
				() -> assertEquals(expenseCategory.getId(), created.getExpenseCategoryId()));
	}

	@Test
	@DisplayName("Throw InvalidExpenseException when trying to create an invalid expense")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenInvalidExpense_WhenCreating_ThenThrowInvalidExpenseException() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				-1000L,
				"USD",
				expenseCategory.getId());

		assertThrowsExactly(InvalidExpenseException.class, () -> underTest.create(createDto));
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to create without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenCreating_ThenThrowForbiddenException() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				1000L,
				"USD",
				expenseCategory.getId());

		assertThrowsExactly(ForbiddenException.class, () -> underTest.create(createDto));
	}

	@Test
	@DisplayName("Throw ExpenseCategoryNotFoundException when trying to create using a non-existing expense category")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenNonExistingExpenseCategory_WhenCreating_ThenThrowExpenseCategoryNotFoundException() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				1000L,
				"USD",
				expenseCategory.getId() + 1L);

		assertThrowsExactly(ExpenseCategoryNotFoundException.class,
				() -> underTest.create(createDto));
	}

}
