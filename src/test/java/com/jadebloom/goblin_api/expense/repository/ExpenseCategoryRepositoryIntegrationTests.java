package com.jadebloom.goblin_api.expense.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import com.jadebloom.goblin_api.expense.entity.ExpenseCategoryEntity;

@DataJpaTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ExpenseCategoryRepositoryIntegrationTests {

    private final ExpenseCategoryRepository underTest;

    @Autowired
    public ExpenseCategoryRepositoryIntegrationTests(ExpenseCategoryRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    public void canCreateAndFindExpenseCategory() {
        ExpenseCategoryEntity entity = new ExpenseCategoryEntity("Daily");
        ExpenseCategoryEntity savedEntity = underTest.save(entity);

        Optional<ExpenseCategoryEntity> foundEntity = underTest.findById(savedEntity.getId());

        assertAll(
                "Assert that an expense category can be created and found",
                () -> assertTrue(foundEntity.isPresent()),
                () -> assertEquals(savedEntity, foundEntity.get()));
    }

    @Test
    public void canCreateAndFindMultipleExpenseCategories() {
        ExpenseCategoryEntity e1 = new ExpenseCategoryEntity("Daily");
        ExpenseCategoryEntity e2 = new ExpenseCategoryEntity("Debt");

        ExpenseCategoryEntity se1 = underTest.save(e1);
        ExpenseCategoryEntity se2 = underTest.save(e2);

        Page<ExpenseCategoryEntity> page = underTest.findAll(PageRequest.of(0, 5));

        List<ExpenseCategoryEntity> entities = page.getContent();

        assertAll(
                "Assert that multiple expense categories can be created and found",
                () -> assertEquals(2, entities.size()),
                () -> assertTrue(entities.contains(se1)),
                () -> assertTrue(entities.contains(se2)));
    }

    @Test
    public void canReturnTrueWhenCheckingExistenceOfExistingExpenseCategory() {
        ExpenseCategoryEntity entity = new ExpenseCategoryEntity("Daily");

        Long id = underTest.save(entity).getId();

        assertTrue(underTest.existsById(id));
    }

    @Test
    public void canReturnFalseWhenCheckingExistenceOfNonExistingExpenseCategory() {
        assertFalse(underTest.existsById(1L));
    }

    @Test
    public void canFullUpdateAndFindExpenseCategory() {
        ExpenseCategoryEntity entity = new ExpenseCategoryEntity("Daily");

        ExpenseCategoryEntity savedEntity = underTest.save(entity);
        savedEntity.setDescription("Daily necessities");

        underTest.save(savedEntity);

        Optional<ExpenseCategoryEntity> foundEntity = underTest.findById(savedEntity.getId());

        assertAll(
                "Assert that an expense category can be full updated and found",
                () -> assertTrue(foundEntity.isPresent()),
                () -> assertEquals(savedEntity, foundEntity.get()));
    }

    @Test
    public void canDeleteAllExpenseCategories() {
        ExpenseCategoryEntity e1 = new ExpenseCategoryEntity("Daily");
        ExpenseCategoryEntity e2 = new ExpenseCategoryEntity("Debt");

        underTest.save(e1);
        underTest.save(e2);

        underTest.deleteAll();

        Page<ExpenseCategoryEntity> page = underTest.findAll(PageRequest.of(0, 5));

        List<ExpenseCategoryEntity> entities = page.getContent();

        assertTrue(entities.isEmpty());
    }

    @Test
    public void canDeleteExpenseCategoryById() {
        ExpenseCategoryEntity entity = new ExpenseCategoryEntity("Daily");

        Long id = underTest.save(entity).getId();

        underTest.deleteById(id);

        Optional<ExpenseCategoryEntity> foundEntity = underTest.findById(id);

        assertTrue(foundEntity.isEmpty());
    }

}
