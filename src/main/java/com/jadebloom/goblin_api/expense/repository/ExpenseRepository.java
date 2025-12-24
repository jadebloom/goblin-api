package com.jadebloom.goblin_api.expense.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.jadebloom.goblin_api.expense.entity.ExpenseEntity;

@Repository
public interface ExpenseRepository
		extends CrudRepository<ExpenseEntity, Long>,
		PagingAndSortingRepository<ExpenseEntity, Long> {

	Page<ExpenseEntity> findAllByCreator_Email(String creatorEmail, Pageable pageable);

	boolean existsByName(String name);

	boolean existsByIdNotAndName(Long id, String name);

	boolean existsByCurrency_Id(Long currencyId);

	boolean existsByExpenseCategory_Id(Long expenseCategoryId);

	boolean existsByIdAndCreator_Email(Long id, String creatorEmail);

}
