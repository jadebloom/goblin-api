package com.jadebloom.goblin_api.expense.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

import com.jadebloom.goblin_api.expense.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.entity.ExpenseCategoryEntity;

@Component
public class ExpenseCategoryMapper {

	private final ModelMapper modelMapper;

	public ExpenseCategoryMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public ExpenseCategoryEntity map(CreateExpenseCategoryDto createExpenseCategoryDto) {
		TypeMap<CreateExpenseCategoryDto, ExpenseCategoryEntity> typeMap = modelMapper.getTypeMap(
				CreateExpenseCategoryDto.class,
				ExpenseCategoryEntity.class);

		if (typeMap == null) {
			typeMap = modelMapper.emptyTypeMap(
					CreateExpenseCategoryDto.class,
					ExpenseCategoryEntity.class);

			typeMap.addMapping(
					CreateExpenseCategoryDto::getName,
					ExpenseCategoryEntity::setName);
			typeMap.addMapping(
					CreateExpenseCategoryDto::getDescription,
					ExpenseCategoryEntity::setDescription);
		}

		return typeMap.map(createExpenseCategoryDto);
	}

	public ExpenseCategoryDto map(ExpenseCategoryEntity expenseCategoryDto) {
		TypeMap<ExpenseCategoryEntity, ExpenseCategoryDto> typeMap = modelMapper.getTypeMap(
				ExpenseCategoryEntity.class,
				ExpenseCategoryDto.class);

		if (typeMap == null) {
			typeMap = modelMapper.createTypeMap(
					ExpenseCategoryEntity.class,
					ExpenseCategoryDto.class);

			typeMap.addMapping(ExpenseCategoryEntity::getId, ExpenseCategoryDto::setId);
			typeMap.addMapping(ExpenseCategoryEntity::getName, ExpenseCategoryDto::setName);
			typeMap.addMapping(
					ExpenseCategoryEntity::getDescription,
					ExpenseCategoryDto::setDescription);
			typeMap.addMapping(
					ExpenseCategoryEntity::getCreatedAt,
					ExpenseCategoryDto::setCreatedAt);

			typeMap.addMappings(mapper -> mapper.map(
					src -> src.getId(), ExpenseCategoryDto::setCreatorId));
		}

		return typeMap.map(expenseCategoryDto);
	}

}
