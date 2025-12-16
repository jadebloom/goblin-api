package com.jadebloom.goblin_api.expense.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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

	private ExpenseCategoryEntity expenseCategoryEntity;
	private CurrencyEntity currencyEntity;

	@Autowired
	public ExpenseRepositoryIntegrationTests(
			ExpenseRepository underTest,
			ExpenseCategoryRepository expenseCategoryRepository,
			CurrencyRepository currencyRepository) {
		this.underTest = underTest;

		this.expenseCategoryRepository = expenseCategoryRepository;
		this.currencyRepository = currencyRepository;
	}

	@BeforeEach
	public void createExpenseCategoryEntity() {
		ExpenseCategoryEntity e = new ExpenseCategoryEntity("Gaming Stuff");

		expenseCategoryEntity = expenseCategoryRepository.save(e);
	}

	@BeforeEach
	public void createCurrencyEntity() {
		CurrencyEntity e = new CurrencyEntity("Gaming Stuff");

		currencyEntity = currencyRepository.save(e);
	}

	@Test
	public void canCreateAndFindExpenses() {
		ExpenseEntity e1 = new ExpenseEntity(
				"Uber Ride",
				1000L,
				expenseCategoryEntity,
				currencyEntity);
		ExpenseEntity e2 = new ExpenseEntity(
				"Uber Ride",
				1000L,
				expenseCategoryEntity,
				currencyEntity);

		ExpenseEntity savedE1 = underTest.save(e1);
		ExpenseEntity savedE2 = underTest.save(e2);

		Page<ExpenseEntity> page = underTest.findAll(PageRequest.of(0, 5));
		List<ExpenseEntity> entities = page.getContent();

		assertAll(
				"Assert that expenses can be created and found",
				() -> assertEquals(2, entities.size()),
				() -> assertTrue(entities.contains(savedE1)),
				() -> assertTrue(entities.contains(savedE2)));
	}

	@Test
	public void canCreateExpenseAndFindItById() {
		ExpenseEntity e = new ExpenseEntity(
				"Uber Ride",
				1000L,
				expenseCategoryEntity,
				currencyEntity);
		ExpenseEntity savedE = underTest.save(e);

		Optional<ExpenseEntity> foundE = underTest.findById(savedE.getId());

		assertAll(
				"Assert that an expense can be created and found",
				() -> assertTrue(foundE.isPresent()),
				() -> assertEquals(savedE, foundE.get()));
	}

	@Test
	public void canUpdateAndFindExpenseById() {
		ExpenseEntity e = new ExpenseEntity(
				"Uber Ride",
				1000L,
				expenseCategoryEntity,
				currencyEntity);
		ExpenseEntity savedE = underTest.save(e);

		savedE.setName("New name for the expense");
		savedE.setLabels(List.of("newLabel1", "newLabeL2"));

		Optional<ExpenseEntity> foundE = underTest.findById(savedE.getId());

		assertAll(
				"Assert that an expense can be updated and found",
				() -> assertTrue(foundE.isPresent()),
				() -> assertEquals(savedE, foundE.get()));
	}

	@Test
	public void canDeleteExpenseById() {
		ExpenseEntity e = new ExpenseEntity(
				"Uber Ride",
				1000L,
				expenseCategoryEntity,
				currencyEntity);
		Long id = underTest.save(e).getId();

		underTest.deleteById(id);

		Optional<ExpenseEntity> foundE = underTest.findById(id);

		assertTrue(foundE.isEmpty());
	}

}
