package com.jadebloom.goblin_api.expense.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jadebloom.goblin_api.expense.entity.ExpenseEntity;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

	Page<ExpenseEntity> findAllByCreator_Email(String creatorEmail, Pageable pageable);

	boolean existsByIdNotAndName(Long id, String name);

	boolean existsByCurrency_Id(Long currencyId);

	boolean existsByExpenseCategory_Id(Long expenseCategoryId);

	boolean existsByIdAndCreator_Email(Long id, String creatorEmail);

}
