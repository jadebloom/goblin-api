package com.jadebloom.goblin_api.expense.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.jadebloom.goblin_api.expense.entity.ExpenseCategoryEntity;

@Repository
public interface ExpenseCategoryRepository extends CrudRepository<ExpenseCategoryEntity, Long>,
                PagingAndSortingRepository<ExpenseCategoryEntity, Long> {
}
