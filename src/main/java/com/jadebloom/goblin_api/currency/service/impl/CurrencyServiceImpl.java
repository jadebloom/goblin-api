package com.jadebloom.goblin_api.currency.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;
import com.jadebloom.goblin_api.currency.repository.CurrencyRepository;
import com.jadebloom.goblin_api.currency.service.CurrencyService;
import com.jadebloom.goblin_api.currency.util.CurrencyEntityValidator;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    public CurrencyServiceImpl(
            CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Override
    public CurrencyEntity save(CurrencyEntity currencyEntity) {
        CurrencyEntityValidator.validateProperty(currencyEntity, "name");
        CurrencyEntityValidator.validateProperty(currencyEntity, "alphabeticalCode");

        return currencyRepository.save(currencyEntity);
    }

    @Override
    public Page<CurrencyEntity> findAll(Pageable pageable) {
        return currencyRepository.findAll(pageable);
    }

    @Override
    public Optional<CurrencyEntity> findById(Long currencyId) {
        return currencyRepository.findById(currencyId);
    }

}
