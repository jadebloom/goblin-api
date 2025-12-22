package com.jadebloom.goblin_api.security.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jadebloom.goblin_api.security.entity.PermissionEntity;

@Repository
public interface PermissionRepository extends CrudRepository<PermissionEntity, Long> {

    Optional<PermissionEntity> findByName(String name);

}
