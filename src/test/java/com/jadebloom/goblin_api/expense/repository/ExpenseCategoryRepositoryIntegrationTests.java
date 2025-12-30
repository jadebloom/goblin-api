package com.jadebloom.goblin_api.expense.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import com.jadebloom.goblin_api.expense.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.test.PermissionTestUtils;
import com.jadebloom.goblin_api.security.test.RoleTestUtils;
import com.jadebloom.goblin_api.security.test.UserTestUtils;

@DataJpaTest
@Import({ UserTestUtils.class, RoleTestUtils.class, PermissionTestUtils.class })
public class ExpenseCategoryRepositoryIntegrationTests {

    private final ExpenseCategoryRepository underTest;

    private final UserTestUtils userTestUtils;

    private UserEntity expenseCategoryCreator;

    @Autowired
    public ExpenseCategoryRepositoryIntegrationTests(
            ExpenseCategoryRepository underTest,
            UserTestUtils userTestUtils) {
        this.underTest = underTest;

        this.userTestUtils = userTestUtils;
    }

    @BeforeEach
    public void createExpenseCategoryCreator() {
        expenseCategoryCreator = userTestUtils.createUser();
    }

    @Test
    @DisplayName("Return expense categories found by their creator's email")
    public void GivenExpenseCategories_WhenFindingThemByTheirCreatorEmail_ThenReturnThem() {
        ExpenseCategoryEntity toCreate1 = new ExpenseCategoryEntity(
                "Daily",
                expenseCategoryCreator);
        ExpenseCategoryEntity toCreate2 = new ExpenseCategoryEntity(
                "Special",
                expenseCategoryCreator);

        ExpenseCategoryEntity created1 = underTest.save(toCreate1);
        ExpenseCategoryEntity created2 = underTest.save(toCreate2);

        Page<ExpenseCategoryEntity> page = underTest.findAllByCreator_Email(
                expenseCategoryCreator.getEmail(),
                PageRequest.of(0, 20));

        List<ExpenseCategoryEntity> expenseCategories = page.getContent();

        assertAll("Assert that expense categories can be found by their creator's email",
                () -> assertEquals(2, expenseCategories.size()),
                () -> assertTrue(expenseCategories.contains(created1)),
                () -> assertTrue(expenseCategories.contains(created2)));
    }

    @Test
    @DisplayName("Return true when checking the existence of an existing expense category by its name")
    public void GivenExpenseCategory_WhenCheckingItsExistenceByName_ThenReturnTrue() {
        ExpenseCategoryEntity toCreate = new ExpenseCategoryEntity(
                "Daily",
                expenseCategoryCreator);
        ExpenseCategoryEntity created = underTest.save(toCreate);

        boolean isExists = underTest.existsByName(created.getName());

        assertTrue(isExists);
    }

    @Test
    @DisplayName("Return true when checking the existence of an existing expense category by its name and creator's email")
    public void GivenExpenseCategory_WhenCheckingItsExistenceByNameAndCreatorEmail_ThenReturnTrue() {
        ExpenseCategoryEntity toCreate = new ExpenseCategoryEntity(
                "Daily",
                expenseCategoryCreator);
        ExpenseCategoryEntity created = underTest.save(toCreate);

        boolean isExists = underTest.existsByIdAndCreator_Email(
                created.getId(),
                expenseCategoryCreator.getEmail());

        assertTrue(isExists);
    }

    @Test
    @DisplayName("Return false when checking the existence of an existing expense category by its name and other creator's email")
    public void GivenExpenseCategory_WhenCheckingItsExistenceByNameAndOtherCreatorEmail_ThenReturnFalse() {
        ExpenseCategoryEntity toCreate = new ExpenseCategoryEntity(
                "Daily",
                expenseCategoryCreator);
        ExpenseCategoryEntity created = underTest.save(toCreate);

        boolean isExists = underTest.existsByIdAndCreator_Email(
                created.getId(),
                expenseCategoryCreator.getEmail() + "m");

        assertFalse(isExists);
    }

    @Test
    @DisplayName("Return true when checking the existence of an existing expense category by its name and other ID")
    public void GivenExpenseCategory_WhenCheckingItsExistenceByNameAndOtherId_ThenReturnTrue() {
        ExpenseCategoryEntity toCreate = new ExpenseCategoryEntity(
                "Daily",
                expenseCategoryCreator);
        ExpenseCategoryEntity created = underTest.save(toCreate);

        boolean isExists = underTest.existsByIdNotAndName(
                created.getId() + 1,
                created.getName());

        assertTrue(isExists);
    }

    @Test
    @DisplayName("Return false when checking the existence of an existing expense category by its name and ID")
    public void GivenExpenseCategory_WhenCheckingItsExistenceByNameAndId_ThenReturnFalse() {
        ExpenseCategoryEntity toCreate = new ExpenseCategoryEntity(
                "Daily",
                expenseCategoryCreator);
        ExpenseCategoryEntity created = underTest.save(toCreate);

        boolean isExists = underTest.existsByIdNotAndName(
                created.getId(),
                created.getName());

        assertFalse(isExists);
    }

}
