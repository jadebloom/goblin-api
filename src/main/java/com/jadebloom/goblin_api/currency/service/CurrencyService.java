package com.jadebloom.goblin_api.currency.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jadebloom.goblin_api.currency.dto.InvalidCurrencyException;
import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;

public interface CurrencyService {

    CurrencyEntity save(CurrencyEntity currencyEntity);

    Page<CurrencyEntity> findAll(Pageable pageable);

    Optional<CurrencyEntity> findById(Long currencyId);

    // boolean existsById(Long id);

    // void deleteAll(List<CurrencyEntity> currencyEntities);

    // void deleteById(Long id);

}
