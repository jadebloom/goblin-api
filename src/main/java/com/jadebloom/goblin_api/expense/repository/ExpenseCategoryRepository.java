package com.jadebloom.goblin_api.expense.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jadebloom.goblin_api.expense.entity.ExpenseCategoryEntity;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategoryEntity, Long> {

	Page<ExpenseCategoryEntity> findAllByCreator_Email(String creatorEmail, Pageable pageable);

	boolean existsByName(String name);

	boolean existsByIdAndCreator_Email(Long id, String creatorEmail);

	boolean existsByIdNotAndName(Long id, String name);

}
