package com.jadebloom.goblin_api.security.test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.jadebloom.goblin_api.security.entity.RoleEntity;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.repository.RoleRepository;
import com.jadebloom.goblin_api.security.repository.UserRepository;

@Component
public class UserTestUtils {

	private final UserRepository userRepository;

	private final RoleRepository roleRepository;

	private final RoleTestUtils roleTestUtils;

	public UserTestUtils(
			UserRepository userRepository,
			RoleRepository roleRepository,
			RoleTestUtils roleTestUtils) {
		this.userRepository = userRepository;

		this.roleRepository = roleRepository;

		this.roleTestUtils = roleTestUtils;
	}

	public UserEntity createUserAndItsDependencies() {
		RoleEntity userRole = roleTestUtils.createUserRole();

		UserEntity toCreate = new UserEntity("user@gmail.com", "123", Set.of(userRole));
		UserEntity created = userRepository.save(toCreate);

		return created;
	}

	public UserEntity createUserWithPossiblyExistingRoles(
			String email,
			String password,
			Set<String> roleNames) {
		Set<RoleEntity> roles = new HashSet<>();

		for (String roleName : roleNames) {
			Optional<RoleEntity> optUserRole = roleRepository.findByName(roleName);

			if (optUserRole.isPresent()) {
				roles.add(optUserRole.get());
			}
		}

		UserEntity toCreate = new UserEntity(email, password, roles);

		return userRepository.save(toCreate);
	}

}
