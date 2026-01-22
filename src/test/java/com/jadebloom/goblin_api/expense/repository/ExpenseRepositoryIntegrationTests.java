package com.jadebloom.goblin_api.expense.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.jadebloom.goblin_api.expense.entity.ExpenseEntity;
import com.jadebloom.goblin_api.expense_category.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense_category.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.test.PermissionTestUtils;
import com.jadebloom.goblin_api.security.test.RoleTestUtils;
import com.jadebloom.goblin_api.security.test.UserTestUtils;

@DataJpaTest(showSql = false)
@Import({ UserTestUtils.class, RoleTestUtils.class, PermissionTestUtils.class })
public class ExpenseRepositoryIntegrationTests {

	private final ExpenseRepository underTest;

	private final ExpenseCategoryRepository expenseCategoryRepository;

	private final UserTestUtils userTestUtils;

	private ExpenseCategoryEntity expenseCategory;

	private UserEntity user;

	@Autowired
	public ExpenseRepositoryIntegrationTests(
			ExpenseRepository underTest,
			ExpenseCategoryRepository expenseCategoryRepository,
			UserTestUtils userTestUtils) {
		this.underTest = underTest;

		this.expenseCategoryRepository = expenseCategoryRepository;

		this.userTestUtils = userTestUtils;
	}

	@BeforeEach
	public void createDependencies() {
		user = userTestUtils.createUserAndItsDependencies();

		ExpenseCategoryEntity toCreate1 = new ExpenseCategoryEntity("Daily", user);
		toCreate1.setDescription("The category description");
		expenseCategory = expenseCategoryRepository.saveAndFlush(toCreate1);
	}

	@Test
	@DisplayName("Return a page with finding expenses by their creator's email")
	public void GivenExpenses_WhenFindingAllExpensesByTheirCreatorEmail_ThenReturnPage() {
		ExpenseEntity toCreate1 = new ExpenseEntity(
				"Uber Ride",
				1000L,
				"USD",
				expenseCategory,
				user);
		ExpenseEntity toCreate2 = new ExpenseEntity(
				"Uber Ride",
				1000L,
				"USD",
				expenseCategory,
				user);

		ExpenseEntity created1 = underTest.saveAndFlush(toCreate1);
		ExpenseEntity created2 = underTest.saveAndFlush(toCreate2);

		Page<ExpenseEntity> page = underTest.findAllByCreator_Id(
				user.getId(),
				PageRequest.of(0, 20));

		List<ExpenseEntity> expenses = page.getContent();

		assertAll("Assert that expenses can be found by their creator's email",
				() -> assertNotNull(page),
				() -> assertNotNull(expenses),
				() -> assertEquals(2, expenses.size()),
				() -> assertTrue(expenses.contains(created1)),
				() -> assertTrue(expenses.contains(created2)));
	}

	@Test
	@DisplayName("Return true when checking the existence of a valid expense by its expense category ID")
	public void GivenValidExpense_WhenCheckingItsExistenceByItsExpenseCategoryId_ThenReturnTrue() {
		ExpenseEntity toCreate = new ExpenseEntity(
				"Uber Ride",
				1000L,
				"USD",
				expenseCategory,
				user);
		Long expenseCategoryId = underTest.save(toCreate).getExpenseCategory().getId();

		boolean isExists = underTest.existsByExpenseCategory_Id(expenseCategoryId);

		assertTrue(isExists);
	}

	@Test
	@DisplayName("Return false when checking the existence of a valid expense non-existing expense category ID")
	public void GivenExpenseWithNonExistingCurrency_WhenCheckingItsExistenceByItsExpenseCategoryId_ThenReturnFalse() {
		boolean isExists = underTest.existsByExpenseCategory_Id(1L);

		assertFalse(isExists);
	}

	@Test
	@DisplayName("Verify that all possible expenses can be deleted by their category's ID")
	public void GivenPossibleExpenses_WhenDeletingAllByExpenseCategoryId_ThenDelete() {
		ExpenseEntity toCreate = new ExpenseEntity(
				"Uber Ride",
				1000L,
				"USD",
				expenseCategory,
				user);
		underTest.save(toCreate);

		underTest.deleteAllByExpenseCategory_Id(expenseCategory.getId());

		List<ExpenseEntity> expenses = underTest.findAll();

		assertEquals(0, expenses.size());
	}

	@Test
	@DisplayName("Verify that all possible expenses can be deleted by their creator's ID")
	public void GivenExpenses_WhenDeletingAllByCreatorId_ThenDelete() {
		ExpenseEntity toCreate = new ExpenseEntity(
				"Uber Ride",
				1000L,
				"USD",
				expenseCategory,
				user);
		underTest.save(toCreate);

		underTest.deleteAllByCreator_Id(user.getId());

		List<ExpenseEntity> expenses = underTest.findAll();

		assertEquals(0, expenses.size());
	}

}
