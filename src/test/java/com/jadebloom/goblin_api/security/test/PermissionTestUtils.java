package com.jadebloom.goblin_api.security.test;

import java.util.List;

import org.springframework.stereotype.Component;

import com.jadebloom.goblin_api.security.entity.PermissionEntity;
import com.jadebloom.goblin_api.security.repository.PermissionRepository;

@Component
public class PermissionTestUtils {

	private final PermissionRepository permissionRepository;

	public PermissionTestUtils(PermissionRepository permissionRepository) {
		this.permissionRepository = permissionRepository;
	}

	public List<PermissionEntity> createExpenseCategoryPermissions() {
		List<String> permissionNames = List.of(
				"CREATE_EXPENSE_CATEGORY",
				"READ_EXPENSE_CATEGORY",
				"UPDATE_EXPENSE_CATEGORY",
				"DELETE_EXPENSE_CATEGORY");

		return createPermissions(permissionNames);
	}

	public List<PermissionEntity> createExpensePermissions() {
		List<String> permissionNames = List.of(
				"CREATE_EXPENSE",
				"READ_EXPENSE",
				"UPDATE_EXPENSE",
				"DELETE_EXPENSE");

		return createPermissions(permissionNames);
	}

	public List<PermissionEntity> createPermissions(List<String> permissionNames) {
		List<PermissionEntity> permissions = permissionNames.stream()
				.map(name -> permissionRepository.save(new PermissionEntity(name)))
				.toList();

		return permissions;
	}

}
