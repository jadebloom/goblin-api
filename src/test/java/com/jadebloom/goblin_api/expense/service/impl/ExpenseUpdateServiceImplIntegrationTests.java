package com.jadebloom.goblin_api.expense.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
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
import com.jadebloom.goblin_api.expense.dto.UpdateExpenseDto;
import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseException;
import com.jadebloom.goblin_api.expense_category.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense_category.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.test.UserTestUtils;

@SpringBootTest
@Transactional
public class ExpenseUpdateServiceImplIntegrationTests {

	private final ExpenseUpdateServiceImpl underTest;

	private final ExpenseCreateServiceImpl createService;

	private final ExpenseCategoryRepository expenseCategoryRepo;

	private final UserTestUtils userTestUtils;

	private ExpenseCategoryEntity expenseCategory;

	private UserEntity user;

	@Autowired
	public ExpenseUpdateServiceImplIntegrationTests(ExpenseUpdateServiceImpl underTest,
			ExpenseCreateServiceImpl createService,
			ExpenseCategoryRepository expenseCategoryRepo,
			UserTestUtils userTestUtils) {
		this.underTest = underTest;

		this.createService = createService;

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
	@DisplayName("Return an expense when updating with valid all fields")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenValidExpenseWithAllFields_WhenUpdating_ThenReturnExpense() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				100L,
				"USD",
				expenseCategory.getId());
		ExpenseDto created = createService.create(createDto);

		UpdateExpenseDto updateDto = new UpdateExpenseDto(
				"Birthday expenses",
				1001L,
				"USD",
				expenseCategory.getId());

		updateDto.setDescription("Magnificent description");

		List<String> labels = new ArrayList<>();
		labels.add("Label1");
		labels.add("Label2");
		updateDto.setLabels(labels);

		ExpenseDto updated = underTest.update(created.getId(), updateDto);

		assertAll("Assert that an expense can be updated and returned with valid all fields",
				() -> assertEquals(created.getId(), updated.getId()),
				() -> assertEquals(updateDto.getName(), updated.getName()),
				() -> assertEquals(updateDto.getDescription(), updated.getDescription()),
				() -> assertEquals(updateDto.getAmount(), updated.getAmount()),
				() -> assertEquals(updateDto.getCurrencyCode(), updated.getCurrencyCode()),
				() -> assertTrue(updateDto.getLabels().equals(updated.getLabels())),
				() -> assertEquals(created.getCreatedAt(), updated.getCreatedAt()),
				() -> assertEquals(expenseCategory.getId(), updated.getExpenseCategoryId()));
	}

	@Test
	@DisplayName("Return an expense when updating with valid only required fields")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenValidExpenseWithOnlyRequiredFields_WhenUpdating_ThenReturnExpense() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				100L,
				"USD",
				expenseCategory.getId());
		ExpenseDto created = createService.create(createDto);

		UpdateExpenseDto updateDto = new UpdateExpenseDto(
				"Birthday expenses",
				1001L,
				"USD",
				expenseCategory.getId());

		ExpenseDto updated = underTest.update(created.getId(), updateDto);

		assertAll("Assert that an expense can be updated and returned with valid all fields",
				() -> assertEquals(created.getId(), updated.getId()),
				() -> assertEquals(updateDto.getName(), updated.getName()),
				() -> assertNull(updated.getDescription()),
				() -> assertEquals(updateDto.getAmount(), updated.getAmount()),
				() -> assertEquals(updateDto.getCurrencyCode(), updated.getCurrencyCode()),
				() -> assertNull(updated.getLabels()),
				() -> assertEquals(created.getCreatedAt(), updated.getCreatedAt()),
				() -> assertEquals(expenseCategory.getId(), updated.getExpenseCategoryId()));
	}

	@Test
	@DisplayName("Throw InvalidExpenseException when trying to update using an invalid expense")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenInvalidExpense_WhenUpdating_ThenThrowInvalidExpenseException() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				100L,
				"USD",
				expenseCategory.getId());
		ExpenseDto created = createService.create(createDto);

		UpdateExpenseDto updateDto = new UpdateExpenseDto(
				"Birthday expenses",
				-1001L,
				"USD",
				expenseCategory.getId());

		assertThrowsExactly(InvalidExpenseException.class,
				() -> underTest.update(created.getId(), updateDto));
	}

	@Test
	@DisplayName("Throw ExpenseNotFoundException when updating a non-existing expense")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenNonExistingExpense_WhenUpdating_ThenThrowExpenseNotFoundException() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				100L,
				"USD",
				expenseCategory.getId());
		ExpenseDto created = createService.create(createDto);

		UpdateExpenseDto updateDto = new UpdateExpenseDto(
				"Birthday expenses",
				1001L,
				"USD",
				expenseCategory.getId());

		assertThrowsExactly(ExpenseNotFoundException.class,
				() -> underTest.update(created.getId() + 1, updateDto));
	}

	@Test
	@DisplayName("Throw ExpenseCategoryNotFoundException when updating an expense using a non-existing expense category ID")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenNonExistingExpenseCategory_WhenUpdating_ThenThrowExpenseCategoryNotFoundException() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				100L,
				"USD",
				expenseCategory.getId());
		ExpenseDto created = createService.create(createDto);

		UpdateExpenseDto updateDto = new UpdateExpenseDto(
				"Birthday expenses",
				1001L,
				"USD",
				expenseCategory.getId() + 1L);

		assertThrowsExactly(ExpenseCategoryNotFoundException.class,
				() -> underTest.update(created.getId(), updateDto));
	}

}
