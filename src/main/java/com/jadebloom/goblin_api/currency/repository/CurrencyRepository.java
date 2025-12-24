package com.jadebloom.goblin_api.currency.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;

@Repository
public interface CurrencyRepository
        extends CrudRepository<CurrencyEntity, Long>, PagingAndSortingRepository<CurrencyEntity, Long> {

    Page<CurrencyEntity> findAllByCreator_Email(String creatorEmail, Pageable pageable);

    boolean existsByName(String name);

    boolean existsByIdNotAndName(Long id, String name);

    boolean existsByIdAndCreator_Email(Long currencyId, String creatorEmail);

}
