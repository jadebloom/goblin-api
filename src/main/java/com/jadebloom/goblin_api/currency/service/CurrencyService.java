package com.jadebloom.goblin_api.currency.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.currency.error.InvalidCurrencyException;

public interface CurrencyService {

    CurrencyDto save(CurrencyDto currencyDto) throws InvalidCurrencyException, CurrencyNotFoundException;

    Page<CurrencyDto> findAll(Pageable pageable);

    CurrencyDto findById(Long currencyId) throws CurrencyNotFoundException;

    boolean existsById(Long currencyId);

    void deleteAll();

    void deleteById(Long currencyId);

}
