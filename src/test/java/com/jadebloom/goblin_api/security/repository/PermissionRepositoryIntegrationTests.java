package com.jadebloom.goblin_api.security.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.jadebloom.goblin_api.security.entity.PermissionEntity;

@DataJpaTest
public class PermissionRepositoryIntegrationTests {

    private final PermissionRepository underTest;

    @Autowired
    public PermissionRepositoryIntegrationTests(PermissionRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    @DisplayName("Save a permission with a given name")
    public void GivenValidName_WhenSaving_ThenSavePermission() {
        PermissionEntity toSave = new PermissionEntity("CREATE_CURRENCY");

        PermissionEntity saved = underTest.save(toSave);

        assertEquals(toSave.getName(), saved.getName());
    }

    @Test
    @DisplayName("Return an existing permission when finding it by its name")
    public void GivenExistingPermission_WhenFindingByName_ThenReturnPermission() {
        PermissionEntity toSave = new PermissionEntity("CREATE_CURRENCY");
        PermissionEntity saved = underTest.save(toSave);

        Optional<PermissionEntity> found = underTest.findByName(saved.getName());

        assertAll("Assert that a permission can be found by name",
                () -> assertTrue(found.isPresent()),
                () -> assertEquals(saved, found.get()));
    }

}
