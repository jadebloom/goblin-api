package com.jadebloom.goblin_api.security.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.jadebloom.goblin_api.security.entity.RoleEntity;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.test.PermissionTestUtils;
import com.jadebloom.goblin_api.security.test.RoleTestUtils;

@DataJpaTest
@Import({ RoleTestUtils.class, PermissionTestUtils.class })
public class UserRepositoryIntegrationTests {

    private final UserRepository underTest;

    private final RoleTestUtils utils;

    private RoleEntity userRole;

    @Autowired
    public UserRepositoryIntegrationTests(UserRepository underTest, RoleTestUtils utils) {
        this.underTest = underTest;

        this.utils = utils;
    }

    @BeforeEach
    public void createUserRole() {
        userRole = utils.createUserRole();
    }

    @Test
    @DisplayName("Return a user when finding it by email")
    public void GivenUser_WhenFindingByEmail_ThenReturnUser() {
        UserEntity toSave = new UserEntity("user@gmail.com", "123", Set.of(userRole));
        UserEntity saved = underTest.save(toSave);

        Optional<UserEntity> found = underTest.findByEmail(saved.getEmail());

        assertAll("Assert that a user can be created and found by its email",
                () -> assertTrue(found.isPresent()),
                () -> assertEquals(saved, found.get()));
    }

    @Test
    @DisplayName("Return true when checking an existing user for existence by email")
    public void GivenUser_WhenCheckingExistenceByEmail_ThenReturnTrue() {
        UserEntity toSave = new UserEntity("user@gmail.com", "123", Set.of(userRole));
        UserEntity saved = underTest.save(toSave);

        boolean isExists = underTest.existsByEmail(saved.getEmail());

        assertTrue(isExists);
    }

}
