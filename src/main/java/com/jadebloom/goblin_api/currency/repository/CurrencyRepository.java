package com.jadebloom.goblin_api.currency.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;

@Repository
public interface CurrencyRepository
        extends CrudRepository<CurrencyEntity, Long>, PagingAndSortingRepository<CurrencyEntity, Long> {

    public boolean existsByName(String name);

    public boolean existsByIdNotAndName(Long id, String name);

}
