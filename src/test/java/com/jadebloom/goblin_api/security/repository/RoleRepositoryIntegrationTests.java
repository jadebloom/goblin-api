package com.jadebloom.goblin_api.security.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.jadebloom.goblin_api.security.entity.PermissionEntity;
import com.jadebloom.goblin_api.security.entity.RoleEntity;
import com.jadebloom.goblin_api.security.test.PermissionTestUtils;

@DataJpaTest
@Import(PermissionTestUtils.class)
public class RoleRepositoryIntegrationTests {

    private final RoleRepository underTest;

    private final PermissionTestUtils utils;

    @Autowired
    public RoleRepositoryIntegrationTests(RoleRepository underTest, PermissionTestUtils utils) {
        this.underTest = underTest;

        this.utils = utils;
    }

    @Test
    @DisplayName("Save a role with given permissions")
    public void GivenPermissions_WhenSaving_ThenSaveRole() {
        Set<PermissionEntity> permissions = new HashSet<>();

        permissions.addAll(utils.createCurrencyPermissions());
        permissions.addAll(utils.createExpenseCategoryPermissions());
        permissions.addAll(utils.createExpensePermissions());

        RoleEntity toSave = new RoleEntity("USER", permissions);

        RoleEntity saved = underTest.save(toSave);

        assertAll("Assert that a user role can be saved with correct permissions",
                () -> assertEquals(toSave.getName(), saved.getName()),
                () -> assertEquals(permissions.size(), saved.getPermissions().size()));
    }

}
