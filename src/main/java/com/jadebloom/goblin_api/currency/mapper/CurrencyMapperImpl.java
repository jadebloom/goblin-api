package com.jadebloom.goblin_api.currency.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;
import com.jadebloom.goblin_api.shared.mapper.Mapper;

@Component
public class CurrencyMapperImpl implements Mapper<CurrencyEntity, CurrencyDto> {

    private final ModelMapper modelMapper;

    public CurrencyMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public CurrencyDto mapTo(CurrencyEntity t) {
        return modelMapper.map(t, CurrencyDto.class);
    }

    @Override
    public CurrencyEntity mapFrom(CurrencyDto s) {
        return modelMapper.map(s, CurrencyEntity.class);
    }

}
