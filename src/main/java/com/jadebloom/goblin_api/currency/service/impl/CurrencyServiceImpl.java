package com.jadebloom.goblin_api.currency.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;
import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.currency.repository.CurrencyRepository;
import com.jadebloom.goblin_api.currency.service.CurrencyService;
import com.jadebloom.goblin_api.currency.util.CurrencyValidators;
import com.jadebloom.goblin_api.shared.mapper.Mapper;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    private final Mapper<CurrencyDto, CurrencyEntity> mapper;

    public CurrencyServiceImpl(
            CurrencyRepository currencyRepository,
            @Qualifier("currencyMapperImpl") Mapper<CurrencyDto, CurrencyEntity> mapper) {
        this.currencyRepository = currencyRepository;

        this.mapper = mapper;
    }

    @Override
    public CurrencyDto save(CurrencyDto currencyDto) {
        CurrencyValidators.validate(currencyDto);

        return mapper.mapFrom(currencyRepository.save(mapper.mapTo(currencyDto)));
    }

    @Override
    public Page<CurrencyDto> findAll(Pageable pageable) {
        Page<CurrencyEntity> page = currencyRepository.findAll(pageable);

        return page.map(mapper::mapFrom);
    }

    @Override
    public CurrencyDto findById(Long currencyId) {
        Optional<CurrencyEntity> entity = currencyRepository.findById(currencyId);

        if (entity.isEmpty()) {
            String f = "Currency with ID=%d wasn't found";

            throw new CurrencyNotFoundException(String.format(f, currencyId));
        }

        return entity.map(mapper::mapFrom).get();
    }

    @Override
    public boolean existsById(Long currencyId) {
        return currencyRepository.existsById(currencyId);
    }

}
