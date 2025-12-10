package com.jadebloom.goblin_api.currency.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.currency.dto.CreateCurrencyDto;
import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;
import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.currency.error.InvalidCurrencyException;
import com.jadebloom.goblin_api.currency.repository.CurrencyRepository;
import com.jadebloom.goblin_api.currency.service.CurrencyService;
import com.jadebloom.goblin_api.currency.validation.CurrencyValidators;
import com.jadebloom.goblin_api.shared.mapper.Mapper;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    private final Mapper mapper;

    public CurrencyServiceImpl(CurrencyRepository currencyRepository, Mapper mapper) {
        this.currencyRepository = currencyRepository;

        this.mapper = mapper;
    }

    @Override
    public CurrencyDto create(CreateCurrencyDto createCurrencyDto) throws InvalidCurrencyException {
        CurrencyValidators.validate(createCurrencyDto);

        CurrencyEntity entity = mapper.map(createCurrencyDto, CurrencyEntity.class);

        return mapper.map(currencyRepository.save(entity), CurrencyDto.class);
    }

    @Override
    public Page<CurrencyDto> findAll(Pageable pageable) {
        Page<CurrencyEntity> page = currencyRepository.findAll(pageable);

        return page.map(e -> mapper.map(e, CurrencyDto.class));
    }

    @Override
    public CurrencyDto findById(Long currencyId) throws CurrencyNotFoundException {
        Optional<CurrencyEntity> entity = currencyRepository.findById(currencyId);

        if (entity.isEmpty()) {
            String f = "Currency with ID=%d wasn't found";

            throw new CurrencyNotFoundException(String.format(f, currencyId));
        }

        return mapper.map(entity.get(), CurrencyDto.class);
    }

    @Override
    public boolean existsById(Long currencyId) {
        return currencyRepository.existsById(currencyId);
    }

    @Override
    public CurrencyDto update(CurrencyDto currencyDto) throws InvalidCurrencyException, CurrencyNotFoundException {
        if (!currencyRepository.existsById(currencyDto.getId())) {
            String f = "Currency with ID=%d doesn't exist";

            throw new CurrencyNotFoundException(String.format(f, currencyDto.getId()));
        }

        CurrencyValidators.validate(currencyDto);

        CurrencyEntity entity = mapper.map(currencyDto, CurrencyEntity.class);

        return mapper.map(currencyRepository.save(entity), CurrencyDto.class);
    }

    @Override
    public void deleteAll() {
        currencyRepository.deleteAll();
    }

    @Override
    public void deleteById(Long currencyId) {
        currencyRepository.deleteById(currencyId);
    }

}
