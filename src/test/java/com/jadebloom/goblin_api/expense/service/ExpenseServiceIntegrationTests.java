package com.jadebloom.goblin_api.expense.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;
import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;
import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.currency.repository.CurrencyRepository;
import com.jadebloom.goblin_api.expense.dto.CreateExpenseDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.dto.UpdateExpenseDto;
import com.jadebloom.goblin_api.expense.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseException;
import com.jadebloom.goblin_api.expense.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.test.UserTestUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

@SpringBootTest
@Transactional
public class ExpenseServiceIntegrationTests {

	private final ExpenseService underTest;

	private final ExpenseCategoryRepository expenseCategoryRepository;

	private final CurrencyRepository currencyRepository;

	private final UserTestUtils userTestUtils;

	private ExpenseCategoryEntity expenseCategory;

	private CurrencyEntity currency;

	private UserEntity user;

	@Autowired
	public ExpenseServiceIntegrationTests(
			ExpenseService underTest,
			ExpenseCategoryRepository expenseCategoryRepository,
			CurrencyRepository currencyRepository,
			UserTestUtils userTestUtils) {
		this.underTest = underTest;

		this.expenseCategoryRepository = expenseCategoryRepository;

		this.currencyRepository = currencyRepository;

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

		CurrencyEntity toCreate2 = new CurrencyEntity("Tenge", user);
		currency = currencyRepository.saveAndFlush(toCreate2);
	}

	@Test
	@DisplayName("Return an expense when creating it with valid all fields")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenValidExpenseWithAllFields_WhenCreating_ThenReturnExpense() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				100L,
				expenseCategory.getId(),
				currency.getId());

		createDto.setDescription("Magnificent description");

		String label1 = "Label1", label2 = "Label2";
		createDto.setLabels(List.of(label1, label2));

		ExpenseDto created = underTest.create(createDto);

		assertAll("Assert that an expense with valid all fields can be created and returned",
				() -> assertNotNull(created.getId()),
				() -> assertEquals(createDto.getName(), created.getName()),
				() -> assertEquals(createDto.getDescription(), created.getDescription()),
				() -> assertEquals(createDto.getAmount(), created.getAmount()),
				() -> assertEquals(2, created.getLabels().size()),
				() -> assertTrue(created.getLabels().contains(label1)),
				() -> assertTrue(created.getLabels().contains(label2)),
				() -> assertNotNull(created.getCreatedAt()),
				() -> assertEquals(expenseCategory.getId(), created.getExpenseCategoryId()),
				() -> assertEquals(currency.getId(), created.getCurrencyId()));
	}

	@Test
	@DisplayName("Return an expense when creating it with only required fields")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenValidExpenseWithOnlyRequiredFields_WhenCreating_ThenReturnExpense() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				100L,
				expenseCategory.getId(),
				currency.getId());

		ExpenseDto created = underTest.create(createDto);

		assertAll("Assert that an expense with valid all fields can be created and returned",
				() -> assertNotNull(created.getId()),
				() -> assertEquals(createDto.getName(), created.getName()),
				() -> assertNull(created.getDescription()),
				() -> assertEquals(createDto.getAmount(), created.getAmount()),
				() -> assertNull(created.getLabels()),
				() -> assertNotNull(created.getCreatedAt()),
				() -> assertEquals(expenseCategory.getId(), created.getExpenseCategoryId()),
				() -> assertEquals(currency.getId(), created.getCurrencyId()));
	}

	@Test
	@DisplayName("Throw InvalidExpenseException when trying to create an invalid expense")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenInvalidExpense_WhenCreating_ThenThrowInvalidExpenseException() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				-1000L,
				expenseCategory.getId(),
				currency.getId());

		assertThrowsExactly(InvalidExpenseException.class, () -> underTest.create(createDto));
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to create without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenCreating_ThenThrowForbiddenException() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				1000L,
				expenseCategory.getId(),
				currency.getId());

		assertThrowsExactly(ForbiddenException.class, () -> underTest.create(createDto));
	}

	@Test
	@DisplayName("Throw ExpenseCategoryNotFoundException when trying to create using a non-existing expense category")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenNonExistingExpenseCategory_WhenCreating_ThenThrowExpenseCategoryNotFoundException() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				1000L,
				expenseCategory.getId() + 1L,
				currency.getId());

		assertThrowsExactly(ExpenseCategoryNotFoundException.class,
				() -> underTest.create(createDto));
	}

	@Test
	@DisplayName("Throw CurrencyNotFoundException when trying to create using a non-existing currency")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenNonExistingCurrency_WhenCreating_ThenThrowCurrencyNotFoundException() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				1000L,
				expenseCategory.getId(),
				currency.getId() + 1L);

		assertThrowsExactly(CurrencyNotFoundException.class, () -> underTest.create(createDto));
	}

	@Test
	@DisplayName("Return a page with expenses when finding the authenticated user's expenses, given they exist")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenExpenses_WhenFindingAuthenticatedUserExpenses_ThenReturnPageWithExpenses() {
		CreateExpenseDto createDto1 = new CreateExpenseDto(
				"Uber Ride",
				1000L,
				expenseCategory.getId(),
				currency.getId());
		CreateExpenseDto createDto2 = new CreateExpenseDto(
				"Uber Ride",
				1000L,
				expenseCategory.getId(),
				currency.getId());

		ExpenseDto created1 = underTest.create(createDto1);
		ExpenseDto created2 = underTest.create(createDto2);

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
				expenseCategory.getId(),
				currency.getId());
		ExpenseDto created = underTest.create(createDto);

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

	@Test
	@DisplayName("Return an expense when updating with valid all fields")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenValidExpenseWithAllFields_WhenUpdating_ThenReturnExpense() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				100L,
				expenseCategory.getId(),
				currency.getId());
		ExpenseDto created = underTest.create(createDto);

		UpdateExpenseDto updateDto = new UpdateExpenseDto(
				"Birthday expenses",
				1001L,
				expenseCategory.getId(),
				currency.getId());

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
				() -> assertTrue(updateDto.getLabels().equals(updated.getLabels())),
				() -> assertEquals(created.getCreatedAt(), updated.getCreatedAt()),
				() -> assertEquals(expenseCategory.getId(), updated.getExpenseCategoryId()),
				() -> assertEquals(currency.getId(), updated.getCurrencyId()));
	}

	@Test
	@DisplayName("Return an expense when updating with valid only required fields")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenValidExpenseWithOnlyRequiredFields_WhenUpdating_ThenReturnExpense() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				100L,
				expenseCategory.getId(),
				currency.getId());
		ExpenseDto created = underTest.create(createDto);

		UpdateExpenseDto updateDto = new UpdateExpenseDto(
				"Birthday expenses",
				1001L,
				expenseCategory.getId(),
				currency.getId());

		ExpenseDto updated = underTest.update(created.getId(), updateDto);

		assertAll("Assert that an expense can be updated and returned with valid all fields",
				() -> assertEquals(created.getId(), updated.getId()),
				() -> assertEquals(updateDto.getName(), updated.getName()),
				() -> assertNull(updated.getDescription()),
				() -> assertEquals(updateDto.getAmount(), updated.getAmount()),
				() -> assertNull(updated.getLabels()),
				() -> assertEquals(created.getCreatedAt(), updated.getCreatedAt()),
				() -> assertEquals(expenseCategory.getId(), updated.getExpenseCategoryId()),
				() -> assertEquals(currency.getId(), updated.getCurrencyId()));
	}

	@Test
	@DisplayName("Throw InvalidExpenseException when trying to update using an invalid expense")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenInvalidExpense_WhenUpdating_ThenThrowInvalidExpenseException() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				100L,
				expenseCategory.getId(),
				currency.getId());
		ExpenseDto created = underTest.create(createDto);

		UpdateExpenseDto updateDto = new UpdateExpenseDto(
				"Birthday expenses",
				-1001L,
				expenseCategory.getId(),
				currency.getId());

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
				expenseCategory.getId(),
				currency.getId());
		ExpenseDto created = underTest.create(createDto);

		UpdateExpenseDto updateDto = new UpdateExpenseDto(
				"Birthday expenses",
				1001L,
				expenseCategory.getId(),
				currency.getId());

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
				expenseCategory.getId(),
				currency.getId());
		ExpenseDto created = underTest.create(createDto);

		UpdateExpenseDto updateDto = new UpdateExpenseDto(
				"Birthday expenses",
				1001L,
				expenseCategory.getId() + 1L,
				currency.getId());

		assertThrowsExactly(ExpenseCategoryNotFoundException.class,
				() -> underTest.update(created.getId(), updateDto));
	}

	@Test
	@DisplayName("Throw CurrencyNotFoundException when updating an expense using a non-existing currency ID")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenNonExistingCurrency_WhenUpdating_ThenThrowCurrencyNotFoundException() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				100L,
				expenseCategory.getId(),
				currency.getId());
		ExpenseDto created = underTest.create(createDto);

		UpdateExpenseDto updateDto = new UpdateExpenseDto(
				"Birthday expenses",
				1001L,
				expenseCategory.getId(),
				currency.getId() + 1L);

		assertThrowsExactly(CurrencyNotFoundException.class,
				() -> underTest.update(created.getId(), updateDto));
	}

	@Test
	@DisplayName("Do not throw when deleting all expenses, regardless if they exist or not")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenPossibleExpenses_WhenDeletingAll_ThenDoNotThrow() {
		CreateExpenseDto createDto1 = new CreateExpenseDto(
				"Uber Ride",
				100L,
				expenseCategory.getId(),
				currency.getId());
		CreateExpenseDto createDto2 = new CreateExpenseDto(
				"Uber Ride",
				100L,
				expenseCategory.getId(),
				currency.getId());

		underTest.create(createDto1);
		underTest.create(createDto2);

		underTest.deleteAll();

		Page<ExpenseDto> page =
				underTest.findUserAuthenticatedExpenses(PageRequest.of(0, 20));

		assertEquals(0, page.getContent().size());
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to delete all expenses without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenDeletingAllExpenses_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class, () -> underTest.deleteAll());
	}

	@Test
	@DisplayName("Do not throw when deleting all possible expenses by their category's ID")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenPossibleExpenses_WhenDeletingAllExpensesByExpenseCategoryId_ThenDoNotThrow() {
		assertDoesNotThrow(
				() -> underTest.deleteAllExpensesByExpenseCategoryId(expenseCategory.getId()));
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to delete all expenses by their category's ID without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenDeletingAllExpensesByExpenseCategoryId_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class,
				() -> underTest.deleteAllExpensesByExpenseCategoryId(expenseCategory.getId()));
	}

	@Test
	@DisplayName("Throw ExpenseCategoryNotFoundException when trying to delete all possible expenses by a non-existing expense category ID")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenNonExistingExpenseCategory_WhenDeletingAllExpensesByExpenseCategoryId_ThenThrowExpenseCategoryNotFoundException() {
		assertThrowsExactly(ExpenseCategoryNotFoundException.class,
				() -> underTest.deleteAllExpensesByExpenseCategoryId(expenseCategory.getId() + 1L));
	}

	@Test
	@DisplayName("Do not throw when deleting all possible expenses by their currency's ID")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenPossibleExpenses_WhenDeletingAllExpensesByCurrencyId_ThenDoNotThrow() {
		assertDoesNotThrow(
				() -> underTest.deleteAllExpensesByCurrencyId(currency.getId()));
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to delete all expenses by their currency's ID without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenDeletingAllExpensesByCurrencyId_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class,
				() -> underTest.deleteAllExpensesByCurrencyId(currency.getId()));
	}

	@Test
	@DisplayName("Throw CurrencyNotFoundException when trying to delete all possible expenses by a non-existing currency ID")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenNonExistingCurrency_WhenDeletingAllExpensesByCurrencyId_ThenThrowCurrencyNotFoundException() {
		assertThrowsExactly(CurrencyNotFoundException.class,
				() -> underTest.deleteAllExpensesByCurrencyId(currency.getId() + 1L));
	}

	@Test
	@DisplayName("Do not throw when deleting an expense by its ID")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenExistingExpense_WhenDeletingById_ThenDoNotThrow() {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				100L,
				expenseCategory.getId(),
				currency.getId());
		ExpenseDto created = underTest.create(createDto);

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
