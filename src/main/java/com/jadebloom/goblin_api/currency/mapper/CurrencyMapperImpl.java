package com.jadebloom.goblin_api.currency.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;
import com.jadebloom.goblin_api.shared.mapper.Mapper;

@Component
public class CurrencyMapperImpl implements Mapper<CurrencyDto, CurrencyEntity> {

    private final ModelMapper modelMapper;

    public CurrencyMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public CurrencyEntity mapTo(CurrencyDto t) {
        return modelMapper.map(t, CurrencyEntity.class);
    }

    @Override
    public CurrencyDto mapFrom(CurrencyEntity s) {
        return modelMapper.map(s, CurrencyDto.class);
    }

}
