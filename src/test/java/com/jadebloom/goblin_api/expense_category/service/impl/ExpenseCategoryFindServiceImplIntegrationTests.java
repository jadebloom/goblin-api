package com.jadebloom.goblin_api.expense_category.service.impl;

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
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

import com.jadebloom.goblin_api.expense_category.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense_category.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense_category.service.ExpenseCategoryCreateService;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.test.UserTestUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

@SpringBootTest
@Transactional
public class ExpenseCategoryFindServiceImplIntegrationTests {

	private final ExpenseCategoryFindServiceImpl underTest;

	private final ExpenseCategoryCreateService createService;

	private final UserTestUtils userTestUtils;

	private UserEntity user;

	@Autowired
	public ExpenseCategoryFindServiceImplIntegrationTests(ExpenseCategoryFindServiceImpl underTest,
			ExpenseCategoryCreateService createService,
			UserTestUtils userTestUtils) {
		this.underTest = underTest;

		this.createService = createService;

		this.userTestUtils = userTestUtils;
	}

	@BeforeEach
	public void createUser() {
		user = userTestUtils.createUserWithPossiblyExistingRoles(
				"user@gmail.com",
				"123",
				Set.of("ROLE_USER"));
	}

	@Test
	@DisplayName("Return a page when finding the authenticated user's expense categories")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenExpenseCategories_WhenFindingAuthenticatedUserExpenseCategories_ThenReturnPage() {
		CreateExpenseCategoryDto createDto1 = new CreateExpenseCategoryDto("Daily");
		CreateExpenseCategoryDto createDto2 = new CreateExpenseCategoryDto("Special");

		ExpenseCategoryDto created1 = createService.create(createDto1);
		ExpenseCategoryDto created2 = createService.create(createDto2);

		Page<ExpenseCategoryDto> page = underTest.findAuthenticatedUserExpenseCategories(
				PageRequest.of(0, 20));

		List<ExpenseCategoryDto> expenseCategories = page.getContent();

		assertAll(
				"Assert that a page is returned when finding the authenticated user's expense categories",
				() -> assertNotNull(page),
				() -> assertNotNull(expenseCategories),
				() -> assertEquals(2, expenseCategories.size()),
				() -> assertTrue(expenseCategories.contains(created1)),
				() -> assertTrue(expenseCategories.contains(created2)));
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to find the authenticated user's expense categories without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenFindingAuthenticatedUserExpenseCategories_ThenThrowForbiddenException() {
		Pageable pageable = PageRequest.of(0, 20);

		assertThrowsExactly(ForbiddenException.class,
				() -> underTest.findAuthenticatedUserExpenseCategories(pageable));
	}

	@Test
	@DisplayName("Return an expense category when finding it by its ID")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenExistingExpenseCategory_WhenFindingById_ThenReturnExpenseCategory() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");
		ExpenseCategoryDto created = createService.create(createDto);

		ExpenseCategoryDto found = underTest.findById(created.getId());

		assertEquals(created, found);
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to find an expense category by its ID without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenFindingById_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class,
				() -> underTest.findById(0L));
	}

	@Test
	@DisplayName("Throw ExpenseCategoryNotFoundException when trying to find a non-existing expense category by ID")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenNonExistingExpenseCategory_WhenFindingById_ThenThrowExpenseCategoryNotFoundException() {
		assertThrowsExactly(ExpenseCategoryNotFoundException.class,
				() -> underTest.findById(0L));
	}

}
