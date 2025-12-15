package com.jadebloom.goblin_api.currency.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

import com.jadebloom.goblin_api.currency.dto.CreateCurrencyDto;
import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.dto.UpdateCurrencyDto;
import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;

@Component
public class CurrencyMapper {

    private final ModelMapper modelMapper;

    public CurrencyMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public CurrencyEntity map(CreateCurrencyDto createDto) {
        TypeMap<CreateCurrencyDto, CurrencyEntity> typeMap = modelMapper.getTypeMap(
                CreateCurrencyDto.class,
                CurrencyEntity.class);

        if (typeMap == null) {
            typeMap = modelMapper.createTypeMap(
                    CreateCurrencyDto.class,
                    CurrencyEntity.class);

            typeMap.addMapping(CreateCurrencyDto::getName, CurrencyEntity::setName);
            typeMap.addMapping(
                    CreateCurrencyDto::getAlphabeticalCode,
                    CurrencyEntity::setAlphabeticalCode);
        }

        return typeMap.map(createDto);
    }

    public CurrencyEntity map(UpdateCurrencyDto updateDto) {
        TypeMap<UpdateCurrencyDto, CurrencyEntity> typeMap = modelMapper.getTypeMap(
                UpdateCurrencyDto.class,
                CurrencyEntity.class);

        if (typeMap == null) {
            typeMap = modelMapper.createTypeMap(
                    UpdateCurrencyDto.class,
                    CurrencyEntity.class);

            typeMap.addMapping(UpdateCurrencyDto::getName, CurrencyEntity::setName);
            typeMap.addMapping(
                    UpdateCurrencyDto::getAlphabeticalCode,
                    CurrencyEntity::setAlphabeticalCode);
        }

        return typeMap.map(updateDto);
    }

    public CurrencyDto map(CurrencyEntity currencyEntity) {
        TypeMap<CurrencyEntity, CurrencyDto> typeMap = modelMapper.getTypeMap(
                CurrencyEntity.class,
                CurrencyDto.class);

        if (typeMap == null) {
            typeMap = modelMapper.createTypeMap(
                    CurrencyEntity.class,
                    CurrencyDto.class);

            typeMap.addMapping(CurrencyEntity::getId, CurrencyDto::setId);
            typeMap.addMapping(CurrencyEntity::getName, CurrencyDto::setName);
            typeMap.addMapping(
                    CurrencyEntity::getAlphabeticalCode,
                    CurrencyDto::setAlphabeticalCode);
        }

        return typeMap.map(currencyEntity);
    }

}
