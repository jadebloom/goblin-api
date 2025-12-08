package com.jadebloom.goblin_api.expense.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.jadebloom.goblin_api.expense.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.shared.mapper.Mapper;

@Component
public class ExpenseCategoryMapperImpl implements Mapper<ExpenseCategoryDto, ExpenseCategoryEntity> {

    private final ModelMapper modelMapper;

    public ExpenseCategoryMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ExpenseCategoryEntity mapTo(ExpenseCategoryDto t) {
        return modelMapper.map(t, ExpenseCategoryEntity.class);
    }

    @Override
    public ExpenseCategoryDto mapFrom(ExpenseCategoryEntity s) {
        return modelMapper.map(s, ExpenseCategoryDto.class);
    }

}
