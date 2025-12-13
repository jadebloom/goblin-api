package com.jadebloom.goblin_api.currency.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jadebloom.goblin_api.currency.dto.CreateCurrencyDto;
import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.error.CurrencyNameUnavailableException;
import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.currency.error.InvalidCurrencyException;

public interface CurrencyService {

    CurrencyDto create(CreateCurrencyDto createCurrencyDto)
            throws InvalidCurrencyException, CurrencyNameUnavailableException;

    Page<CurrencyDto> findAll(Pageable pageable);

    CurrencyDto findById(Long currencyId) throws CurrencyNotFoundException;

    boolean existsById(Long currencyId);

    boolean existsByName(String name);

    boolean existsByIdNotAndName(Long id, String name);

    CurrencyDto update(CurrencyDto currencyDto)
            throws InvalidCurrencyException, CurrencyNotFoundException, CurrencyNameUnavailableException;

    void deleteAll();

    void deleteById(Long currencyId);

}
