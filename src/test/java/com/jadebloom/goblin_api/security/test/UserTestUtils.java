package com.jadebloom.goblin_api.security.test;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.jadebloom.goblin_api.security.entity.RoleEntity;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.repository.UserRepository;

@Component
public class UserTestUtils {

    private final UserRepository userRepository;

    private final RoleTestUtils roleTestUtils;

    public UserTestUtils(UserRepository userRepository, RoleTestUtils roleTestUtils) {
        this.userRepository = userRepository;

        this.roleTestUtils = roleTestUtils;
    }

    public UserEntity createUser() {
        RoleEntity userRole = roleTestUtils.createUserRole();

        UserEntity toCreate = new UserEntity("user@gmail.com", "123", Set.of(userRole));
        UserEntity created = userRepository.save(toCreate);

        return created;
    }

}
