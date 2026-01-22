package com.jadebloom.goblin_api.expense.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;
import com.jadebloom.goblin_api.expense.dto.CreateExpenseDto;
import com.jadebloom.goblin_api.expense.dto.DeleteExpensesDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;
import com.jadebloom.goblin_api.expense.service.ExpenseCreateService;
import com.jadebloom.goblin_api.expense.service.ExpenseDeleteService;
import com.jadebloom.goblin_api.expense.service.ExpenseFindService;
import com.jadebloom.goblin_api.expense_category.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense_category.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.test.UserTestUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

@SpringBootTest
@Transactional
public class ExpenseDeleteServiceImplIntegrationTests {

	private final ExpenseDeleteService underTest;

	private final ExpenseCreateService createService;

	private final ExpenseFindService findService;

	private final ExpenseCategoryRepository expenseCategoryRepository;

	private final UserTestUtils userTestUtils;

	private ExpenseCategoryEntity expenseCategory;

	private UserEntity user;

	@Autowired
	public ExpenseDeleteServiceImplIntegrationTests(ExpenseDeleteService underTest,
			ExpenseCreateService createService,
			ExpenseFindService findService,
			ExpenseCategoryRepository expenseCategoryRepository,
			UserTestUtils userTestUtils) {
		this.underTest = underTest;

		this.createService = createService;

		this.findService = findService;

		this.expenseCategoryRepository = expenseCategoryRepository;

		this.userTestUtils = userTestUtils;
	}

	@BeforeEach
	public void createDependencies() {
		user = userTestUtils.createUserWithPossiblyExistingRoles(
				"user@gmail.com",
				"123",
				Set.of("ROLE_USER"));

		ExpenseCategoryEntity toCreate1 = new ExpenseCategoryEntity("Daily", user);
		expenseCategory = expenseCategoryRepository.saveAndFlush(toCreate1);
	}

	@Test
	@DisplayName("Do not throw when deleting all expenses, regardless if they exist or not")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenPossibleExpenses_WhenDeletingAll_ThenDoNotThrow() {
		CreateExpenseDto createDto1 = new CreateExpenseDto(
				"Uber Ride",
				100L,
				"USD",
				expenseCategory.getId());
		CreateExpenseDto createDto2 = new CreateExpenseDto(
				"Uber Ride",
				100L,
				"USD",
				expenseCategory.getId());

		createService.create(createDto1);
		createService.create(createDto2);

		underTest.deleteAll();

		Page<ExpenseDto> page = findService.findUserAuthenticatedExpenses(PageRequest.of(0, 20));

		assertEquals(0, page.getContent().size());
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to delete all expenses without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenDeletingAllExpenses_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class, () -> underTest.deleteAll());
	}

	@Test
	@DisplayName("Do not throw when deleting all expenses by ID, regardless if they exist or not")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenPossibleExpenses_WhenDeletingAllById_ThenDoNotThrow() {
		CreateExpenseDto createDto1 = new CreateExpenseDto(
				"Uber Ride",
				100L,
				"USD",
				expenseCategory.getId());
		CreateExpenseDto createDto2 = new CreateExpenseDto(
				"Uber Ride",
				100L,
				"USD",
				expenseCategory.getId());

		ExpenseDto created1 = createService.create(createDto1);
		ExpenseDto created2 = createService.create(createDto2);

		DeleteExpensesDto deleteDto = new DeleteExpensesDto(List.of(created1.getId(), created2.getId()));

		underTest.deleteSelectedById(deleteDto);

		Page<ExpenseDto> page = findService.findUserAuthenticatedExpenses(PageRequest.of(0, 20));

		assertEquals(0, page.getContent().size());
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to delete all expenses by ID without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenDeletingAllById_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class, () -> underTest.deleteAll());
	}

	@Test
	@DisplayName("Do not throw when deleting all possible expenses by their category's ID")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenPossibleExpenses_WhenDeletingAllExpensesByExpenseCategoryId_ThenDoNotThrow() {
		assertDoesNotThrow(
				() -> underTest.deleteAllByExpenseCategoryId(expenseCategory.getId()));
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to delete all expenses by their category's ID without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenDeletingAllExpensesByExpenseCategoryId_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class,
				() -> underTest.deleteAllByExpenseCategoryId(expenseCategory.getId()));
	}

	@Test
	@DisplayName("Throw ExpenseCategoryNotFoundException when trying to delete all possible expenses by a non-existing expense category ID")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenNonExistingExpenseCategory_WhenDeletingAllExpensesByExpenseCategoryId_ThenThrowExpenseCategoryNotFoundException() {
		assertThrowsExactly(ExpenseCategoryNotFoundException.class,
				() -> underTest.deleteAllByExpenseCategoryId(expenseCategory.getId() + 1L));
	}

	@Test
	@DisplayName("Do not throw when deleting an expense by its ID")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenExistingExpense_WhenDeletingById_ThenDoNotThrow() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				100L,
				"USD",
				expenseCategory.getId());
		ExpenseDto created = createService.create(createDto);

		assertDoesNotThrow(() -> underTest.deleteById(created.getId()));
	}

	@Test
	@DisplayName("Throw ExpenseNotFoundException when trying to delete a non-existing expense by ID")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenNonExistingExpense_WhenDeletingById_ThenThrowExpenseNotFoundException() {
		assertThrowsExactly(ExpenseNotFoundException.class, () -> underTest.deleteById(1L));
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to delete by ID without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenDeletingById_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class, () -> underTest.deleteById(1L));
	}

}
