package com.jadebloom.goblin_api.currency.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.currency.error.InvalidCurrencyException;

public interface CurrencyService {

    CurrencyDto save(CurrencyDto currencyDto) throws InvalidCurrencyException;

    Page<CurrencyDto> findAll(Pageable pageable);

    CurrencyDto findById(Long currencyId) throws CurrencyNotFoundException;

    boolean existsById(Long currencyId);

    // void deleteAll(List<CurrencyDto> currencyDtos);

    // void deleteById(Long currencyId);

}
