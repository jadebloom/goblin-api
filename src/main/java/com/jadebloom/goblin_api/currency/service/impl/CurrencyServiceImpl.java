package com.jadebloom.goblin_api.currency.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.currency.dto.CreateCurrencyDto;
import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;
import com.jadebloom.goblin_api.currency.error.CurrencyNameUnavailableException;
import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.currency.error.InvalidCurrencyException;
import com.jadebloom.goblin_api.currency.mapper.CurrencyMapper;
import com.jadebloom.goblin_api.currency.repository.CurrencyRepository;
import com.jadebloom.goblin_api.currency.service.CurrencyService;
import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    private final CurrencyMapper mapper;

    public CurrencyServiceImpl(CurrencyRepository currencyRepository, CurrencyMapper mapper) {
        this.currencyRepository = currencyRepository;

        this.mapper = mapper;
    }

    @Override
    public CurrencyDto create(CreateCurrencyDto createCurrencyDto)
            throws InvalidCurrencyException, CurrencyNameUnavailableException {
        if (!GenericValidator.isValid(createCurrencyDto)) {
            String message = GenericValidator.getValidationErrorMessage(createCurrencyDto);

            throw new InvalidCurrencyException(message);
        }

        if (currencyRepository.existsByName(createCurrencyDto.getName())) {
            String f = "Currency with name=%s already exists";
            String errorMessage = String.format(f, createCurrencyDto.getName());

            throw new CurrencyNameUnavailableException(errorMessage);
        }

        CurrencyEntity entity = mapper.map(createCurrencyDto);

        return mapper.map(currencyRepository.save(entity));
    }

    @Override
    public Page<CurrencyDto> findAll(Pageable pageable) {
        Page<CurrencyEntity> page = currencyRepository.findAll(pageable);

        return page.map(mapper::map);
    }

    @Override
    public CurrencyDto findById(Long currencyId) throws CurrencyNotFoundException {
        Optional<CurrencyEntity> entity = currencyRepository.findById(currencyId);

        if (entity.isEmpty()) {
            String f = "Currency with ID=%d wasn't found";

            throw new CurrencyNotFoundException(String.format(f, currencyId));
        }

        return mapper.map(entity.get());
    }

    @Override
    public boolean existsById(Long currencyId) {
        return currencyRepository.existsById(currencyId);
    }

    @Override
    public boolean existsByName(String name) {
        return currencyRepository.existsByName(name);
    }

    @Override
    public boolean existsByIdNotAndName(Long id, String name) {
        return currencyRepository.existsByIdNotAndName(id, name);
    }

    @Override
    public CurrencyDto update(CurrencyDto currencyDto)
            throws InvalidCurrencyException, CurrencyNotFoundException, CurrencyNameUnavailableException {
        if (!GenericValidator.isValid(currencyDto)) {
            String message = GenericValidator.getValidationErrorMessage(currencyDto);

            throw new InvalidCurrencyException(message);
        }

        Long id = currencyDto.getId();
        String name = currencyDto.getName();

        if (!existsById(id)) {
            String f = "Currency with ID=%d doesn't exist";

            throw new CurrencyNotFoundException(String.format(f, id));
        }

        if (currencyRepository.existsByIdNotAndName(id, name)) {
            String f = "Other currency with name=%s already exists";
            String errorMessage = String.format(f, name);

            throw new CurrencyNameUnavailableException(errorMessage);
        }

        CurrencyEntity entity = mapper.map(currencyDto);

        return mapper.map(currencyRepository.save(entity));
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
