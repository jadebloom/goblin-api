package com.jadebloom.goblin_api.currency.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jadebloom.goblin_api.currency.dto.CreateCurrencyDto;
import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.dto.UpdateCurrencyDto;
import com.jadebloom.goblin_api.currency.error.CurrencyInUseException;
import com.jadebloom.goblin_api.currency.error.CurrencyNameUnavailableException;
import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

public interface CurrencyService {

	CurrencyDto create(CreateCurrencyDto createDto)
			throws ForbiddenException, CurrencyNameUnavailableException;

	Page<CurrencyDto> findAuthenticatedUserCurrencies(Pageable pageable) throws ForbiddenException;

	CurrencyDto findById(Long currencyId) throws ForbiddenException, CurrencyNotFoundException;

	boolean existsById(Long currencyId) throws ForbiddenException;

	CurrencyDto update(UpdateCurrencyDto updateDto)
			throws ForbiddenException, CurrencyNotFoundException, CurrencyNameUnavailableException;

	void deleteById(Long currencyId) throws ForbiddenException, CurrencyInUseException;

}
