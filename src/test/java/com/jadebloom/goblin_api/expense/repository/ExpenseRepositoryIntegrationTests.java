package com.jadebloom.goblin_api.expense.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;
import com.jadebloom.goblin_api.currency.repository.CurrencyRepository;
import com.jadebloom.goblin_api.expense.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense.entity.ExpenseEntity;

@DataJpaTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ExpenseRepositoryIntegrationTests {

	private final ExpenseRepository underTest;

	private final ExpenseCategoryRepository expenseCategoryRepository;

	private final CurrencyRepository currencyRepository;

	@Autowired
	public ExpenseRepositoryIntegrationTests(
			ExpenseRepository underTest,
			ExpenseCategoryRepository expenseCategoryRepository,
			CurrencyRepository currencyRepository) {
		this.underTest = underTest;

		this.expenseCategoryRepository = expenseCategoryRepository;

		this.currencyRepository = currencyRepository;
	}

	@Test
	public void canCreateAndFindExpenseById() {
		ExpenseCategoryEntity expenseCategoryEntity = saveExpenseCategoryEntity("Daily");
		CurrencyEntity currencyEntity = saveCurrencyEntity("Dollar");

		ExpenseEntity entity = createExampleExpenseEntityWithDependencies(
				expenseCategoryEntity,
				currencyEntity);

		ExpenseEntity savedEntity = underTest.save(entity);

		Optional<ExpenseEntity> foundEntity = underTest.findById(savedEntity.getId());

		assertAll(
				"Assert that an expense can be created and found",
				() -> assertTrue(foundEntity.isPresent()),
				() -> assertEquals(savedEntity, foundEntity.get()));
	}

	@Test
	public void canCreateAndFindExpenses() {
		ExpenseCategoryEntity expenseCategoryEntity = saveExpenseCategoryEntity("Daily");
		CurrencyEntity currencyEntity = saveCurrencyEntity("Dollar");

		ExpenseEntity entity1 = createExampleExpenseEntityWithDependencies(
				expenseCategoryEntity,
				currencyEntity);
		ExpenseEntity entity2 = createExampleExpenseEntityWithDependencies(
				expenseCategoryEntity,
				currencyEntity);

		ExpenseEntity savedEntity1 = underTest.save(entity1);
		ExpenseEntity savedEntity2 = underTest.save(entity2);

		Page<ExpenseEntity> page = underTest.findAll(PageRequest.of(0, 5));
		List<ExpenseEntity> entities = page.getContent();

		assertAll(
				"Assert that expenses can be created and found",
				() -> assertEquals(2, entities.size()),
				() -> assertTrue(entities.contains(savedEntity1)),
				() -> assertTrue(entities.contains(savedEntity2)));
	}

	@Test
	public void canUpdateAndFindExpenseById() {
		ExpenseCategoryEntity expenseCategoryEntity = saveExpenseCategoryEntity("Daily");
		CurrencyEntity currencyEntity = saveCurrencyEntity("Dollar");
		ExpenseEntity entity = createExampleExpenseEntityWithDependencies(
				expenseCategoryEntity,
				currencyEntity);

		ExpenseEntity savedEntity = underTest.save(entity);
		savedEntity.setName("New name for the expense");
		savedEntity.setLabels(List.of("newLabel1", "newLabeL2"));

		Optional<ExpenseEntity> foundEntity = underTest.findById(savedEntity.getId());

		assertAll(
				"Assert that an expense can be created, updated and found",
				() -> assertTrue(foundEntity.isPresent()),
				() -> assertEquals(savedEntity, foundEntity.get()));
	}

	@Test
	public void canDeleteAllExpenses() {
		ExpenseCategoryEntity expenseCategoryEntity = saveExpenseCategoryEntity("Daily");
		CurrencyEntity currencyEntity = saveCurrencyEntity("Dollar");

		ExpenseEntity entity1 = createExampleExpenseEntityWithDependencies(
				expenseCategoryEntity,
				currencyEntity);
		ExpenseEntity entity2 = createExampleExpenseEntityWithDependencies(
				expenseCategoryEntity,
				currencyEntity);

		underTest.save(entity1);
		underTest.save(entity2);

		underTest.deleteAll();

		Page<ExpenseEntity> page = underTest.findAll(PageRequest.of(0, 20));

		assertTrue(page.getContent().isEmpty());
	}

	@Test
	public void canDeleteExpense() {
		ExpenseCategoryEntity expenseCategoryEntity = saveExpenseCategoryEntity("Daily");
		CurrencyEntity currencyEntity = saveCurrencyEntity("Dollar");

		ExpenseEntity entity = createExampleExpenseEntityWithDependencies(
				expenseCategoryEntity,
				currencyEntity);
		Long id = underTest.save(entity).getId();

		underTest.deleteById(id);

		Optional<ExpenseEntity> foundEntity = underTest.findById(id);

		assertTrue(foundEntity.isEmpty());
	}

	private ExpenseEntity createExampleExpenseEntityWithDependencies(
			ExpenseCategoryEntity expenseCategoryEntity,
			CurrencyEntity currencyEntity) {
		ExpenseEntity expenseEntity = new ExpenseEntity(
				"Uber Ride",
				1000,
				expenseCategoryEntity,
				currencyEntity);

		return expenseEntity;
	}

	private ExpenseCategoryEntity saveExpenseCategoryEntity(String name) {
		ExpenseCategoryEntity entity = new ExpenseCategoryEntity(name);

		return expenseCategoryRepository.save(entity);
	}

	private CurrencyEntity saveCurrencyEntity(String name) {
		CurrencyEntity entity = new CurrencyEntity(name);

		return currencyRepository.save(entity);
	}

}
