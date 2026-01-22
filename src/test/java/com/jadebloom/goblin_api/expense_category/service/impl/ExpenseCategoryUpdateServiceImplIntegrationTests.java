package com.jadebloom.goblin_api.expense_category.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.jadebloom.goblin_api.expense_category.dto.UpdateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNameUnavailableException;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense_category.error.InvalidExpenseCategoryException;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.test.UserTestUtils;

@SpringBootTest
@Transactional
public class ExpenseCategoryUpdateServiceImplIntegrationTests {

	private final ExpenseCategoryUpdateServiceImpl underTest;

	private final ExpenseCategoryCreateServiceImpl createService;

	private final UserTestUtils userTestUtils;

	private UserEntity user;

	@Autowired
	public ExpenseCategoryUpdateServiceImplIntegrationTests(ExpenseCategoryUpdateServiceImpl underTest,
			ExpenseCategoryCreateServiceImpl createService,
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
	@DisplayName(("Return an expense category when updating with valid all fields"))
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenValidExpenseCategoryWithAllFields_WhenUpdating_ThenReturnExpenseCategory() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");
		ExpenseCategoryDto created = createService.create(createDto);

		UpdateExpenseCategoryDto updateDto = new UpdateExpenseCategoryDto("Debt");
		updateDto.setDescription("Daily expenses category");
		updateDto.setHexColorCode("#FFF");

		ExpenseCategoryDto updated = underTest.update(created.getId(), updateDto);

		assertAll("Assert that an expense category can be updated given valid all fields",
				() -> assertEquals(created.getId(), updated.getId()),
				() -> assertEquals(updateDto.getName(), updated.getName()),
				() -> assertEquals(updateDto.getDescription(), updated.getDescription()),
				() -> assertEquals(updateDto.getHexColorCode(), updated.getHexColorCode()),
				() -> assertEquals(created.getCreatedAt(), updated.getCreatedAt()),
				() -> assertEquals(created.getCreatorId(), updated.getCreatorId()));
	}

	@Test
	@DisplayName(("Return an expense category when updating with valid only required fields"))
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenValidExpenseCategoryWithOnlyRequiredFields_WhenUpdating_ThenReturnExpenseCategory() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");
		ExpenseCategoryDto created = createService.create(createDto);

		UpdateExpenseCategoryDto updateDto = new UpdateExpenseCategoryDto("Debt");

		ExpenseCategoryDto updated = underTest.update(created.getId(), updateDto);

		assertAll("Assert that an expense category can be updated given valid only required fields",
				() -> assertEquals(created.getId(), updated.getId()),
				() -> assertEquals(updateDto.getName(), updated.getName()),
				() -> assertEquals(updateDto.getDescription(), updated.getDescription()),
				() -> assertEquals(created.getCreatedAt(), updated.getCreatedAt()),
				() -> assertEquals(created.getCreatorId(), updated.getCreatorId()));
	}

	@Test
	@DisplayName("Throw InvalidExpenseCategoryException when trying to update using an invalid expense category")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenInvalidExpenseCategory_WhenUpdating_ThenReturnThrowInvalidExpenseCategory() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");
		ExpenseCategoryDto created = createService.create(createDto);

		UpdateExpenseCategoryDto updateDto = new UpdateExpenseCategoryDto("  ");

		assertThrowsExactly(InvalidExpenseCategoryException.class,
				() -> underTest.update(created.getId(), updateDto));
	}

	@Test
	@DisplayName("Throw ExpenseCategoryNameUnavailableException when trying to update using an inavailable name")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenUnavailableName_WhenUpdating_ThenThrowExpenseCategoryNameUnavailable() {
		CreateExpenseCategoryDto createDto1 = new CreateExpenseCategoryDto("Daily");
		createService.create(createDto1);

		CreateExpenseCategoryDto createDto2 = new CreateExpenseCategoryDto("Debt");
		ExpenseCategoryDto created2 = createService.create(createDto2);

		UpdateExpenseCategoryDto updateDto = new UpdateExpenseCategoryDto(createDto1.getName());

		assertThrowsExactly(ExpenseCategoryNameUnavailableException.class,
				() -> underTest.update(created2.getId(), updateDto));
	}

	@Test
	@DisplayName("Throw ExpenseCategoryNotFoundException when trying to update a non-existing expense category")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenNonExistingExpenseCategory_WhenUpdating_ThrowExpenseCategoryNotFoundException() {
		UpdateExpenseCategoryDto updateDto = new UpdateExpenseCategoryDto("Daily");

		assertThrowsExactly(ExpenseCategoryNotFoundException.class,
				() -> underTest.update(1L, updateDto));
	}

}
