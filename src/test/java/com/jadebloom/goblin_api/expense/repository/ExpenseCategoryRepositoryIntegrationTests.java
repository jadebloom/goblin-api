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
    public void canCreateAndFindExpenseCategories() {
        ExpenseCategoryEntity e1 = new ExpenseCategoryEntity("Daily");
        ExpenseCategoryEntity e2 = new ExpenseCategoryEntity("Debt");

        ExpenseCategoryEntity savedE1 = underTest.save(e1);
        ExpenseCategoryEntity savedE2 = underTest.save(e2);

        Page<ExpenseCategoryEntity> page = underTest.findAll(PageRequest.of(0, 5));
        List<ExpenseCategoryEntity> entities = page.getContent();

        assertAll(
                "Assert that expense categories can be created and found",
                () -> assertEquals(2, entities.size()),
                () -> assertTrue(entities.contains(savedE1)),
                () -> assertTrue(entities.contains(savedE2)));
    }

    @Test
    public void canCreateExpenseCategoryAndFindItById() {
        ExpenseCategoryEntity e = new ExpenseCategoryEntity("Daily");
        ExpenseCategoryEntity savedE = underTest.save(e);

        Optional<ExpenseCategoryEntity> foundEntity = underTest.findById(savedE.getId());

        assertAll(
                "Assert that an expense category can be created and found",
                () -> assertTrue(foundEntity.isPresent()),
                () -> assertEquals(savedE, foundEntity.get()));
    }

    @Test
    public void canCheckExpenseCategoryForExistenceByName() {
        ExpenseCategoryEntity e = new ExpenseCategoryEntity("Dollar");
        ExpenseCategoryEntity savedE = underTest.save(e);

        boolean b1 = underTest.existsByName(savedE.getName());
        boolean b2 = underTest.existsByName(savedE.getName() + 1);

        assertAll("Assert that expense categories can be checked for existence by name",
                () -> assertTrue(b1),
                () -> assertFalse(b2));
    }

    @Test
    public void canCheckExpenseCategoryForExistenceByIdNotAndName() {
        ExpenseCategoryEntity e = new ExpenseCategoryEntity("Daily");
        ExpenseCategoryEntity savedE = underTest.save(e);

        boolean b1 = underTest.existsByIdNotAndName(savedE.getId(), savedE.getName());
        boolean b2 = underTest.existsByIdNotAndName(savedE.getId() + 1, savedE.getName());

        assertAll("Assert that expense categories can be checked for existence by name and not id",
                () -> assertFalse(b1),
                () -> assertTrue(b2));
    }

    @Test
    public void canUpdateExpenseCategoryAndFindItById() {
        ExpenseCategoryEntity e = new ExpenseCategoryEntity("Daily");
        ExpenseCategoryEntity savedE = underTest.save(e);

        savedE.setDescription("Daily necessities");
        underTest.save(savedE);

        Optional<ExpenseCategoryEntity> foundE = underTest.findById(savedE.getId());

        assertAll(
                "Assert that an expense category can be updated and found",
                () -> assertTrue(foundE.isPresent()),
                () -> assertEquals(savedE, foundE.get()));
    }

    @Test
    public void canDeleteExpenseCategoryById() {
        ExpenseCategoryEntity e = new ExpenseCategoryEntity("Daily");
        Long id = underTest.save(e).getId();

        underTest.deleteById(id);

        Optional<ExpenseCategoryEntity> foundE = underTest.findById(id);

        assertTrue(foundE.isEmpty());
    }

}
