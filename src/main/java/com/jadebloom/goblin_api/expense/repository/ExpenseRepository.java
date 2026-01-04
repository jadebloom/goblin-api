package com.jadebloom.goblin_api.expense.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jadebloom.goblin_api.expense.entity.ExpenseEntity;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

	Page<ExpenseEntity> findAllByCreator_Id(Long creatorId, Pageable pageable);

	boolean existsByExpenseCategory_Id(Long expenseCategoryId);

	boolean existsByCurrency_Id(Long currencyId);

	void deleteAllByExpenseCategory_Id(Long expenseCategoryId);

	void deleteAllByCreator_Id(Long creatorId);

	void deleteAllByCurrency_Id(Long currencyId);

}
