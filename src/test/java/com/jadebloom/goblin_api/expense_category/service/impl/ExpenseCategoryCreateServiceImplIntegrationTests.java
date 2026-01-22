package com.jadebloom.goblin_api.expense_category.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

import com.jadebloom.goblin_api.expense_category.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense_category.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNameUnavailableException;
import com.jadebloom.goblin_api.expense_category.error.InvalidExpenseCategoryException;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.test.UserTestUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

@SpringBootTest
@Transactional
public class ExpenseCategoryCreateServiceImplIntegrationTests {

	private final ExpenseCategoryCreateServiceImpl underTest;

	private final UserTestUtils userTestUtils;

	private UserEntity user;

	@Autowired
	public ExpenseCategoryCreateServiceImplIntegrationTests(ExpenseCategoryCreateServiceImpl underTest,
			UserTestUtils userTestUtils) {
		this.underTest = underTest;

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
	@DisplayName("Return an expense category when creating it with all valid fields")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenValidExpenseCategoryWithAllFields_WhenCreating_ThenReturnExpenseCategory() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");
		createDto.setDescription("Magnificent ride");
		createDto.setHexColorCode("#FF0000");

		ExpenseCategoryDto created = underTest.create(createDto);

		assertAll("Assert that an expense category can be created using valid all fields",
				() -> assertNotNull(created.getId()),
				() -> assertEquals(createDto.getName(), created.getName()),
				() -> assertEquals(createDto.getDescription(), created.getDescription()),
				() -> assertEquals(createDto.getHexColorCode(), created.getHexColorCode()),
				() -> assertNotNull(created.getCreatedAt()),
				() -> assertEquals(user.getId(), created.getCreatorId()));
	}

	@Test
	@DisplayName("Return an expense category when creating it with only required fields")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenValidExpenseCategoryWithOnlyRequiredFields_WhenCreating_ThenReturnExpenseCategory() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");
		ExpenseCategoryDto created = underTest.create(createDto);

		assertAll("Assert that an expense category can be created using valid all fields",
				() -> assertNotNull(created.getId()),
				() -> assertEquals(createDto.getName(), created.getName()),
				() -> assertEquals(createDto.getDescription(), created.getDescription()),
				() -> assertNotNull(created.getCreatedAt()),
				() -> assertEquals(user.getId(), created.getCreatorId()));
	}

	@Test
	@DisplayName("Throw InvalidExpenseCategoryException when trying to create an expense category with invalid fields")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenInvalidExpenseCategory_WhenCreating_ThenThrowInvalidExpenseCategoryException() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto(" ");

		assertThrowsExactly(InvalidExpenseCategoryException.class,
				() -> underTest.create(createDto));
	}

	@Test
	@DisplayName("Throw ExpenseCategoryNameUnavailableException when creating using an unavailable name")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenUnavailableName_WhenCreating_ThenThrowExpenseCategoryNameUnavailable() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");

		underTest.create(createDto);

		assertThrowsExactly(ExpenseCategoryNameUnavailableException.class,
				() -> underTest.create(createDto));
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to create without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenCreating_ThenThrowForbiddenException() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");

		assertThrowsExactly(ForbiddenException.class, () -> underTest.create(createDto));
	}

}
