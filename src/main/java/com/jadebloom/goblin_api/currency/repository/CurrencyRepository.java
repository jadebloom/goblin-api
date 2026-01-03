package com.jadebloom.goblin_api.currency.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, Long> {

	Page<CurrencyEntity> findAllByCreator_Id(Long creatorId, Pageable pageable);

	boolean existsByName(String name);

	boolean existsByIdNotAndName(Long id, String name);

}
