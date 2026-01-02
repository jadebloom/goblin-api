package com.jadebloom.goblin_api.expense.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import com.jadebloom.goblin_api.expense.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.UpdateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNameUnavailableException;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseCategoryException;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.test.UserTestUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

@SpringBootTest
@Transactional
public class ExpenseCategoryServiceIntegrationTests {

	private final ExpenseCategoryService expenseCategoryService;

	private final UserTestUtils userTestUtils;

	private UserEntity user;

	@Autowired
	public ExpenseCategoryServiceIntegrationTests(
			ExpenseCategoryService expenseCategoryService,
			UserTestUtils userTestUtils) {
		this.expenseCategoryService = expenseCategoryService;

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
	@WithMockUser(username = "user@gmail.com")
	public void GivenValidExpenseCategoryWithAllFields_WhenCreating_ThenReturnExpenseCategory() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");
		createDto.setDescription("Magnificent ride");

		ExpenseCategoryDto created = expenseCategoryService.create(createDto);

		assertAll("Assert that an expense category can be created using valid all fields",
				() -> assertNotNull(created.getId()),
				() -> assertEquals(createDto.getName(), created.getName()),
				() -> assertEquals(createDto.getDescription(), created.getDescription()),
				() -> assertNotNull(created.getCreatedAt()),
				() -> assertEquals(user.getId(), created.getCreatorId()));
	}

	@Test
	@DisplayName("Return an expense category when creating it with only required fields")
	@WithMockUser(username = "user@gmail.com")
	public void GivenValidExpenseCategoryWithOnlyRequiredFields_WhenCreating_ThenReturnExpenseCategory() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");
		ExpenseCategoryDto created = expenseCategoryService.create(createDto);

		assertAll("Assert that an expense category can be created using valid all fields",
				() -> assertNotNull(created.getId()),
				() -> assertEquals(createDto.getName(), created.getName()),
				() -> assertEquals(createDto.getDescription(), created.getDescription()),
				() -> assertNotNull(created.getCreatedAt()),
				() -> assertEquals(user.getId(), created.getCreatorId()));
	}

	@Test
	@DisplayName("Throw InvalidExpenseCategoryException when trying to create an expense category with invalid fields")
	@WithMockUser(username = "user@gmail.com")
	public void GivenInvalidExpenseCategory_WhenCreating_ThenThrowInvalidExpenseCategoryException() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto(" ");

		assertThrowsExactly(InvalidExpenseCategoryException.class,
				() -> expenseCategoryService.create(createDto));
	}

	@Test
	@DisplayName("Throw ExpenseCategoryNameUnavailableException when creating using an unavailable name")
	@WithMockUser(username = "user@gmail.com")
	public void GivenUnavailableName_WhenCreating_ThenThrowExpenseCategoryNameUnavailable() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");

		expenseCategoryService.create(createDto);

		assertThrowsExactly(ExpenseCategoryNameUnavailableException.class,
				() -> expenseCategoryService.create(createDto));
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to create without the authenticated user email")
	public void GivenWithoutAuthenticatedUserEmail_WhenCreating_ThenThrowForbiddenException() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");

		assertThrowsExactly(ForbiddenException.class, () -> expenseCategoryService.create(createDto));
	}

	@Test
	@DisplayName("Return a page when finding the authenticated user's expense categories")
	@WithMockUser(username = "user@gmail.com")
	public void GivenExpenseCategories_WhenFindingAuthenticatedUserExpenseCategories_ThenReturnPage() {
		CreateExpenseCategoryDto createDto1 = new CreateExpenseCategoryDto("Daily");
		CreateExpenseCategoryDto createDto2 = new CreateExpenseCategoryDto("Special");

		ExpenseCategoryDto created1 = expenseCategoryService.create(createDto1);
		ExpenseCategoryDto created2 = expenseCategoryService.create(createDto2);

		Page<ExpenseCategoryDto> page = expenseCategoryService.findAuthenticatedUserExpenseCategories(
				PageRequest.of(0, 20));

		List<ExpenseCategoryDto> expenseCategories = page.getContent();

		assertAll("Assert that a page is returned when finding the authenticated user's expense categories",
				() -> assertNotNull(page),
				() -> assertNotNull(expenseCategories),
				() -> assertEquals(2, expenseCategories.size()),
				() -> assertTrue(expenseCategories.contains(created1)),
				() -> assertTrue(expenseCategories.contains(created2)));
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to find the authenticated user's expense categories without its email")
	public void GivenWithoutAuthenticatedUserEmail_WhenFindingAuthenticatedUserExpenseCategories_ThenThrowForbiddenException() {
		Pageable pageable = PageRequest.of(0, 20);

		assertThrowsExactly(ForbiddenException.class,
				() -> expenseCategoryService.findAuthenticatedUserExpenseCategories(pageable));
	}

	@Test
	@DisplayName("Return an expense category when finding it by its ID")
	@WithMockUser(username = "user@gmail.com")
	public void GivenExistingExpenseCategory_WhenFindingById_ThenReturnExpenseCategory() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");
		ExpenseCategoryDto created = expenseCategoryService.create(createDto);

		ExpenseCategoryDto found = expenseCategoryService.findById(created.getId());

		assertEquals(created, found);
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to find an expense category by its ID without the authenticated user's email")
	public void GivenWithoutAuthenticatedUserEmail_WhenFindingById_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class,
				() -> expenseCategoryService.findById(0L));
	}

	@Test
	@DisplayName("Throw ExpenseCategoryNotFoundException when trying to find a non-existing expense category by ID")
	@WithMockUser(username = "user@gmail.com")
	public void GivenNonExistingExpenseCategory_WhenFindingById_ThenThrowExpenseCategoryNotFoundException() {
		assertThrowsExactly(ExpenseCategoryNotFoundException.class,
				() -> expenseCategoryService.findById(0L));
	}

	@Test
	@DisplayName("Return true when checking the existence of an existing expense category by its ID")
	@WithMockUser(username = "user@gmail.com")
	public void GivenExistingExpenseCategory_WhenCheckingItsExistenceById_ReturnTrue() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");
		ExpenseCategoryDto created = expenseCategoryService.create(createDto);

		boolean isExists = expenseCategoryService.existsById(created.getId());

		assertTrue(isExists);
	}

	@Test
	@DisplayName("Return false when checking the existence of a non-existing expense category by its ID")
	@WithMockUser(username = "user@gmail.com")
	public void GivenNonExistingExpenseCategory_WhenCheckingItsExistenceById_ReturnFalse() {
		boolean isExists = expenseCategoryService.existsById(0L);

		assertFalse(isExists);
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to check the existence of an expense category without the authenticated user's email")
	public void GivenWithoutAuthenticatedUserEmail_WhenCheckingExistenceByID_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class, () -> expenseCategoryService.existsById(0L));
	}

	@Test
	@DisplayName(("Return an expense category when updating with valid all fields"))
	@WithMockUser(username = "user@gmail.com")
	public void GivenValidExpenseCategoryWithAllFields_WhenUpdating_ThenReturnExpenseCategory() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");
		ExpenseCategoryDto created = expenseCategoryService.create(createDto);

		UpdateExpenseCategoryDto updateDto = new UpdateExpenseCategoryDto(
				created.getId(),
				"Debt");
		updateDto.setDescription("Daily expenses category");

		ExpenseCategoryDto updated = expenseCategoryService.update(updateDto);

		assertAll("Assert that an expense category can be updated given valid all fields",
				() -> assertEquals(created.getId(), updated.getId()),
				() -> assertEquals(updateDto.getName(), updated.getName()),
				() -> assertEquals(updateDto.getDescription(), updated.getDescription()),
				() -> assertEquals(created.getCreatedAt(), updated.getCreatedAt()),
				() -> assertEquals(created.getCreatorId(), updated.getCreatorId()));
	}

	@Test
	@DisplayName(("Return an expense category when updating with valid only required fields"))
	@WithMockUser(username = "user@gmail.com")
	public void GivenValidExpenseCategoryWithOnlyRequiredFields_WhenUpdating_ThenReturnExpenseCategory() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");
		ExpenseCategoryDto created = expenseCategoryService.create(createDto);

		UpdateExpenseCategoryDto updateDto = new UpdateExpenseCategoryDto(
				created.getId(),
				"Debt");

		ExpenseCategoryDto updated = expenseCategoryService.update(updateDto);

		assertAll("Assert that an expense category can be updated given valid only required fields",
				() -> assertEquals(created.getId(), updated.getId()),
				() -> assertEquals(updateDto.getName(), updated.getName()),
				() -> assertEquals(updateDto.getDescription(), updated.getDescription()),
				() -> assertEquals(created.getCreatedAt(), updated.getCreatedAt()),
				() -> assertEquals(created.getCreatorId(), updated.getCreatorId()));
	}

	@Test
	@DisplayName("Throw InvalidExpenseCategoryException when trying to update using an invalid expense category")
	@WithMockUser(username = "user@gmail.com")
	public void GivenInvalidExpenseCategory_WhenUpdating_ThenReturnThrowInvalidExpenseCategory() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");
		ExpenseCategoryDto created = expenseCategoryService.create(createDto);

		UpdateExpenseCategoryDto updateDto = new UpdateExpenseCategoryDto(
				created.getId(),
				"  ");

		assertThrowsExactly(InvalidExpenseCategoryException.class,
				() -> expenseCategoryService.update(updateDto));
	}

	@Test
	@DisplayName("Throw ExpenseCategoryNameUnavailableException when trying to update using an inavailable name")
	@WithMockUser(username = "user@gmail.com")
	public void GivenUnavailableName_WhenUpdating_ThenThrowExpenseCategoryNameUnavailable() {
		CreateExpenseCategoryDto createDto1 = new CreateExpenseCategoryDto("Daily");
		expenseCategoryService.create(createDto1);

		CreateExpenseCategoryDto createDto2 = new CreateExpenseCategoryDto("Debt");
		ExpenseCategoryDto created2 = expenseCategoryService.create(createDto2);

		UpdateExpenseCategoryDto updateDto = new UpdateExpenseCategoryDto(
				created2.getId(),
				createDto1.getName());

		assertThrowsExactly(ExpenseCategoryNameUnavailableException.class,
				() -> expenseCategoryService.update(updateDto));
	}

	@Test
	@DisplayName("Throw ExpenseCategoryNotFoundException when trying to update a non-existing expense category")
	@WithMockUser(username = "user@gmail.com")
	public void GivenNonExistingExpenseCategory_WhenUpdating_ThrowExpenseCategoryNotFoundException() {
		UpdateExpenseCategoryDto updateDto = new UpdateExpenseCategoryDto(0L, "Daily");

		assertThrowsExactly(ExpenseCategoryNotFoundException.class,
				() -> expenseCategoryService.update(updateDto));
	}

	@Test
	@DisplayName("Do not throw when deleting an expense category by its ID, regardless if it exists or not")
	@WithMockUser(username = "user@gmail.com")
	public void GivenPossibleExpenseCategory_WhenDeletingItById_ThenDoNotThrow() {
		assertDoesNotThrow(() -> expenseCategoryService.deleteById(0L));
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to delete an expense category by its ID without the authenticated user's email")
	public void GivenWithoutAuthenticatedUserEmail_WhenDeletingById_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class,
				() -> expenseCategoryService.deleteById(1L));
	}

}
