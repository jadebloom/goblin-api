package com.jadebloom.goblin_api.currency.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.currency.dto.CreateCurrencyDto;
import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.dto.UpdateCurrencyDto;
import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;
import com.jadebloom.goblin_api.currency.error.CurrencyInUseException;
import com.jadebloom.goblin_api.currency.error.CurrencyNameUnavailableException;
import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.currency.mapper.CurrencyMapper;
import com.jadebloom.goblin_api.currency.repository.CurrencyRepository;
import com.jadebloom.goblin_api.currency.service.CurrencyService;
import com.jadebloom.goblin_api.expense.repository.ExpenseRepository;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    private final ExpenseRepository expenseRepository;

    private final CurrencyMapper mapper;

    public CurrencyServiceImpl(
            CurrencyRepository currencyRepository,
            ExpenseRepository expenseRepository,
            CurrencyMapper mapper) {
        this.currencyRepository = currencyRepository;

        this.expenseRepository = expenseRepository;

        this.mapper = mapper;
    }

    @Override
    public CurrencyDto create(CreateCurrencyDto createDto) throws CurrencyNameUnavailableException {
        if (currencyRepository.existsByName(createDto.getName())) {
            String f = "Currency with the name '%s' already exists";
            String errorMessage = String.format(f, createDto.getName());

            throw new CurrencyNameUnavailableException(errorMessage);
        }

        CurrencyEntity created = mapper.map(createDto);

        return mapper.map(currencyRepository.save(created));
    }

    @Override
    public Page<CurrencyDto> findAll(Pageable pageable) {
        Page<CurrencyEntity> page = currencyRepository.findAll(pageable);

        return page.map(mapper::map);
    }

    @Override
    public CurrencyDto findById(Long currencyId) throws CurrencyNotFoundException {
        Optional<CurrencyEntity> found = currencyRepository.findById(currencyId);

        if (found.isEmpty()) {
            String f = "Currency with the ID '%d' wasn't found";

            throw new CurrencyNotFoundException(String.format(f, currencyId));
        }

        return mapper.map(found.get());
    }

    @Override
    public boolean existsById(Long currencyId) {
        return currencyRepository.existsById(currencyId);
    }

    @Override
    public CurrencyDto update(UpdateCurrencyDto updateDto)
            throws CurrencyNotFoundException, CurrencyNameUnavailableException {
        Optional<CurrencyEntity> optional = currencyRepository.findById(updateDto.getId());

        if (optional.isEmpty()) {
            String f = "Currency with the ID '%d' doesn't exist";

            throw new CurrencyNotFoundException(String.format(f, updateDto.getId()));
        }

        CurrencyEntity entity = optional.get();

        if (currencyRepository.existsByIdNotAndName(entity.getId(), updateDto.getName())) {
            String f = "Other currency with name \"%s\" already exists";
            String errorMessage = String.format(f, updateDto.getName());

            throw new CurrencyNameUnavailableException(errorMessage);
        }

        entity.setName(updateDto.getName());
        entity.setAlphabeticalCode(updateDto.getAlphabeticalCode());

        return mapper.map(currencyRepository.save(entity));
    }

    @Override
    public void deleteById(Long currencyId) throws CurrencyInUseException {
        if (expenseRepository.existsByCurrency_Id(currencyId)) {
            String f = "Cannot delete the currency with the ID '%d': some amount of expenses depend use it";
            String errorMessage = String.format(f, currencyId);

            throw new CurrencyInUseException(errorMessage);
        }

        currencyRepository.deleteById(currencyId);
    }

}
