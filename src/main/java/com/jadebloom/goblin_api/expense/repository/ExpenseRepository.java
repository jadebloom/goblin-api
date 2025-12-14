package com.jadebloom.goblin_api.expense.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.jadebloom.goblin_api.expense.entity.ExpenseEntity;

@Repository
public interface ExpenseRepository
		extends CrudRepository<ExpenseEntity, Long>,
		PagingAndSortingRepository<ExpenseEntity, Long> {

	boolean existsByName(String name);

	boolean existsByIdNotAndName(Long id, String name);

	boolean existsByCurrency_Id(Long currencyId);

}
