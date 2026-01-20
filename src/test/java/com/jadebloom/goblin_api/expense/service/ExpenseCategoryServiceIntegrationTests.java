package com.jadebloom.goblin_api.expense.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

	private final ExpenseCategoryService underTest;

	private final UserTestUtils userTestUtils;

	private UserEntity user;

	@Autowired
	public ExpenseCategoryServiceIntegrationTests(
			ExpenseCategoryService underTest,
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

	@Test
	@DisplayName("Return a page when finding the authenticated user's expense categories")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenExpenseCategories_WhenFindingAuthenticatedUserExpenseCategories_ThenReturnPage() {
		CreateExpenseCategoryDto createDto1 = new CreateExpenseCategoryDto("Daily");
		CreateExpenseCategoryDto createDto2 = new CreateExpenseCategoryDto("Special");

		ExpenseCategoryDto created1 = underTest.create(createDto1);
		ExpenseCategoryDto created2 = underTest.create(createDto2);

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
		ExpenseCategoryDto created = underTest.create(createDto);

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

	@Test
	@DisplayName(("Return an expense category when updating with valid all fields"))
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenValidExpenseCategoryWithAllFields_WhenUpdating_ThenReturnExpenseCategory() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");
		ExpenseCategoryDto created = underTest.create(createDto);

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
		ExpenseCategoryDto created = underTest.create(createDto);

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
		ExpenseCategoryDto created = underTest.create(createDto);

		UpdateExpenseCategoryDto updateDto = new UpdateExpenseCategoryDto("  ");

		assertThrowsExactly(InvalidExpenseCategoryException.class,
				() -> underTest.update(created.getId(), updateDto));
	}

	@Test
	@DisplayName("Throw ExpenseCategoryNameUnavailableException when trying to update using an inavailable name")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenUnavailableName_WhenUpdating_ThenThrowExpenseCategoryNameUnavailable() {
		CreateExpenseCategoryDto createDto1 = new CreateExpenseCategoryDto("Daily");
		underTest.create(createDto1);

		CreateExpenseCategoryDto createDto2 = new CreateExpenseCategoryDto("Debt");
		ExpenseCategoryDto created2 = underTest.create(createDto2);

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

	@Test
	@DisplayName("Do not throw when deleting all expense categories, regardless if they exist or not")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenPossibleExpenseCategories_WhenDeletingAll_ThenDoNotThrow() {
		CreateExpenseCategoryDto createDto1 = new CreateExpenseCategoryDto("Daily");
		CreateExpenseCategoryDto createDto2 = new CreateExpenseCategoryDto("Debt");

		underTest.create(createDto1);
		underTest.create(createDto2);

		underTest.deleteAll();

		Page<ExpenseCategoryDto> page =
				underTest.findAuthenticatedUserExpenseCategories(PageRequest.of(0, 20));

		assertEquals(0, page.getContent().size());
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to delete all expense categories without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenDeletingAll_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class, () -> underTest.deleteAll());
	}

	@Test
	@DisplayName("Do not throw when deleting an expense category by its ID, regardless if it exists or not")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenExistingExpenseCategory_WhenDeletingItById_ThenDoNotThrow() {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");
		ExpenseCategoryDto created = underTest.create(createDto);

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
