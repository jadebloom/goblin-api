package com.jadebloom.goblin_api.currency.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jadebloom.goblin_api.currency.dto.CreateCurrencyDto;
import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.error.CurrencyInUseException;
import com.jadebloom.goblin_api.currency.error.CurrencyNameUnavailableException;
import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;

public interface CurrencyService {

	CurrencyDto create(CreateCurrencyDto createDto) throws CurrencyNameUnavailableException;

	Page<CurrencyDto> findAll(Pageable pageable);

	CurrencyDto findById(Long currencyId) throws CurrencyNotFoundException;

	boolean existsById(Long currencyId);

	CurrencyDto update(CurrencyDto dto)
			throws CurrencyNotFoundException, CurrencyNameUnavailableException;

	void deleteById(Long currencyId) throws CurrencyInUseException;

}
