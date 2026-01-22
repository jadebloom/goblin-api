package com.jadebloom.goblin_api.shared.config;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.jadebloom.goblin_api.security.entity.PermissionEntity;
import com.jadebloom.goblin_api.security.entity.RoleEntity;
import com.jadebloom.goblin_api.security.repository.PermissionRepository;
import com.jadebloom.goblin_api.security.repository.RoleRepository;

@Component
public class DataLoader implements CommandLineRunner {

	private final RoleRepository roleRepository;

	private final PermissionRepository permissionRepository;

	public DataLoader(RoleRepository roleRepository, PermissionRepository permissionRepository) {
		this.roleRepository = roleRepository;

		this.permissionRepository = permissionRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		PermissionEntity createExpenseCategory = loadPermission("CREATE_EXPENSE_CATEGORY");
		PermissionEntity readExpenseCategory = loadPermission("READ_EXPENSE_CATEGORY");
		PermissionEntity updateExpenseCategory = loadPermission("UPDATE_EXPENSE_CATEGORY");
		PermissionEntity deleteExpenseCategory = loadPermission("VIEW_EXPENSE_CATEGORY");

		PermissionEntity createExpense = loadPermission("CREATE_EXPENSE");
		PermissionEntity readExpense = loadPermission("READ_EXPENSE");
		PermissionEntity updateExpense = loadPermission("UPDATE_EXPENSE");
		PermissionEntity deleteExpense = loadPermission("VIEW_EXPENSE");

		Set<PermissionEntity> userPermissions = new HashSet<>();

		userPermissions.add(createExpenseCategory);
		userPermissions.add(readExpenseCategory);
		userPermissions.add(updateExpenseCategory);
		userPermissions.add(deleteExpenseCategory);

		userPermissions.add(createExpense);
		userPermissions.add(readExpense);
		userPermissions.add(updateExpense);
		userPermissions.add(deleteExpense);

		loadRole("ROLE_USER", userPermissions);
	}

	private PermissionEntity loadPermission(String name) {
		Optional<PermissionEntity> opt = permissionRepository.findByName(name);

		return opt.isPresent() ? opt.get() : permissionRepository.save(new PermissionEntity(name));
	}

	private RoleEntity loadRole(String name, Set<PermissionEntity> permissions) {
		Optional<RoleEntity> opt = roleRepository.findByName(name);

		if (opt.isPresent()) {
			return opt.get();
		}

		RoleEntity newRole = new RoleEntity(name, permissions);

		return roleRepository.save(newRole);
	}

}
