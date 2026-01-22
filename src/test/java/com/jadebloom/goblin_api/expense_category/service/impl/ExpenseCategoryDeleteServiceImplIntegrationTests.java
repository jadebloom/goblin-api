package com.jadebloom.goblin_api.expense_category.service.impl;

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

import com.jadebloom.goblin_api.expense_category.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense_category.dto.DeleteExpenseCategoriesDto;
import com.jadebloom.goblin_api.expense_category.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.test.UserTestUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

@SpringBootTest
@Transactional
public class ExpenseCategoryDeleteServiceImplIntegrationTests {

	private final ExpenseCategoryDeleteServiceImpl underTest;

	private final ExpenseCategoryCreateServiceImpl createService;

	private final ExpenseCategoryFindServiceImpl findService;

	private final UserTestUtils userTestUtils;

	private UserEntity user;

	@Autowired
	public ExpenseCategoryDeleteServiceImplIntegrationTests(ExpenseCategoryDeleteServiceImpl underTest,
			ExpenseCategoryCreateServiceImpl createService,
			ExpenseCategoryFindServiceImpl findService,
			UserTestUtils userTestUtils) {
		this.underTest = underTest;

		this.createService = createService;

		this.findService = findService;

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
	@DisplayName("Do not throw when deleting all expense categories, regardless if they exist or not")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenPossibleExpenseCategories_WhenDeletingAll_ThenDoNotThrow() {
		CreateExpenseCategoryDto createDto1 = new CreateExpenseCategoryDto("Daily");
		CreateExpenseCategoryDto createDto2 = new CreateExpenseCategoryDto("Debt");

		createService.create(createDto1);
		createService.create(createDto2);

		underTest.deleteAll();

		Page<ExpenseCategoryDto> page = findService.findAuthenticatedUserExpenseCategories(PageRequest.of(0, 20));

		assertEquals(0, page.getContent().size());
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to delete all expense categories without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenDeletingAll_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class, () -> underTest.deleteAll());
	}

	@Test
	@DisplayName("Do not throw when deleting selected expense categories by ID, regardless if they exist or not")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenPossibleExpenseCategories_WhenDeletingAllById_ThenDoNotThrow() {
		CreateExpenseCategoryDto createDto1 = new CreateExpenseCategoryDto("Daily");
		CreateExpenseCategoryDto createDto2 = new CreateExpenseCategoryDto("Debt");

		ExpenseCategoryDto created1 = createService.create(createDto1);
		ExpenseCategoryDto created2 = createService.create(createDto2);

		DeleteExpenseCategoriesDto deleteDto = new DeleteExpenseCategoriesDto(
				List.of(created1.getId(), created2.getId()));

		underTest.deleteSelectedById(deleteDto);

		Page<ExpenseCategoryDto> page = findService.findAuthenticatedUserExpenseCategories(PageRequest.of(0, 20));

		assertEquals(0, page.getContent().size());
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to delete all expense categories by ID without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenDeletingAllById_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class, () -> underTest.deleteAll());
	}

	@Test
	@DisplayName("Do not throw when deleting an expense category by its ID, regardless if it exists or not")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenExistingExpenseCategory_WhenDeletingItById_ThenDoNotThrow() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");
		ExpenseCategoryDto created = createService.create(createDto);

		assertDoesNotThrow(() -> underTest.deleteById(created.getId()));
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to delete an expense category by its ID without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenDeletingById_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class,
				() -> underTest.deleteById(1L));
	}

	@Test
	@DisplayName("Throw ExpenseCategoryNotFoundException when trying to delete a non-existing expense category by ID")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenNonExistingExpenseCategory_WhenDeletingById_ThenThrowExpenseCategoryNotFoundException() {
		assertThrowsExactly(ExpenseCategoryNotFoundException.class,
				() -> underTest.deleteById(1L));
	}

}
