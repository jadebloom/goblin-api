package com.jadebloom.goblin_api.expense_category.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.jadebloom.goblin_api.expense_category.entity.ExpenseCategoryEntity;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategoryEntity, Long> {

	Page<ExpenseCategoryEntity> findAllByCreator_Id(Long creatorId, Pageable pageable);

	boolean existsByName(String name);

	boolean existsByIdNotAndName(Long id, String name);

	void deleteAllByCreator_Id(Long creatorId);

}
