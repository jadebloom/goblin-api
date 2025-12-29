package com.jadebloom.goblin_api.security.test;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.jadebloom.goblin_api.security.entity.PermissionEntity;
import com.jadebloom.goblin_api.security.entity.RoleEntity;
import com.jadebloom.goblin_api.security.repository.RoleRepository;

@Component
public class RoleTestUtils {

    private final RoleRepository roleRepository;

    private final PermissionTestUtils utils;

    public RoleTestUtils(RoleRepository roleRepository, PermissionTestUtils utils) {
        this.roleRepository = roleRepository;

        this.utils = utils;
    }

    public RoleEntity createUserRole() {
        Set<PermissionEntity> permissions = new HashSet<>();

        permissions.addAll(utils.createCurrencyPermissions());
        permissions.addAll(utils.createExpenseCategoryPermissions());
        permissions.addAll(utils.createExpensePermissions());

        RoleEntity toCreate = new RoleEntity("USER", permissions);

        return roleRepository.save(toCreate);
    }

}
