package com.jadebloom.goblin_api.expense.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;
import com.jadebloom.goblin_api.expense_category.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense_category.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.test.UserTestUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

@SpringBootTest
@Transactional
public class ExpenseFindServiceImplIntegrationTests {

	private final ExpenseFindServiceImpl underTest;

	private final ExpenseCreateServiceImpl createService;

	private final ExpenseCategoryRepository expenseCategoryRepo;

	private final UserTestUtils userTestUtils;

	private ExpenseCategoryEntity expenseCategory;

	private UserEntity user;

	@Autowired
	public ExpenseFindServiceImplIntegrationTests(ExpenseFindServiceImpl underTest,
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
	@DisplayName("Return a page with expenses when finding the authenticated user's expenses, given they exist")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenExpenses_WhenFindingAuthenticatedUserExpenses_ThenReturnPageWithExpenses() {
		CreateExpenseDto createDto1 = new CreateExpenseDto(
				"Uber Ride",
				1000L,
				"USD",
				expenseCategory.getId());
		CreateExpenseDto createDto2 = new CreateExpenseDto(
				"Uber Ride",
				1000L,
				"USD",
				expenseCategory.getId());

		ExpenseDto created1 = createService.create(createDto1);
		ExpenseDto created2 = createService.create(createDto2);

		Page<ExpenseDto> page = underTest.findUserAuthenticatedExpenses(
				PageRequest.of(0, 20));

		List<ExpenseDto> expenses = page.getContent();

		assertAll(
				"Assert that a page with expenses is returned when finding the authenticated user's expenses, given they exist",
				() -> assertNotNull(page),
				() -> assertNotNull(expenses),
				() -> assertEquals(2, expenses.size()),
				() -> assertTrue(expenses.contains(created1)),
				() -> assertTrue(expenses.contains(created2)));
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to find the authenticated user's expenses without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenFindingAuthenticatedUserExpenses_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class,
				() -> underTest.findUserAuthenticatedExpenses(PageRequest.of(0, 20)));
	}

	@Test
	@DisplayName("Return an expense when finding it by ID")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenExistingExpense_WhenFindingById_ThenReturnExpense() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				1000L,
				"USD",
				expenseCategory.getId());
		ExpenseDto created = createService.create(createDto);

		ExpenseDto found = underTest.findById(created.getId());

		assertEquals(created, found);
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to find an expense by ID without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenFindingById_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class, () -> underTest.findById(1L));
	}

	@Test
	@DisplayName("Throw ExpenseNotFoundException when trying to find a non-existing expense by ID")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenNonExistingExpense_WhenFindingById_ThenThrowExpenseNotFoundException() {
		assertThrowsExactly(ExpenseNotFoundException.class, () -> underTest.findById(1L));
	}

}
