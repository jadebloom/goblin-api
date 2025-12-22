package com.jadebloom.goblin_api.security.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.jadebloom.goblin_api.security.entity.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

}
