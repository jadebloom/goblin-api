package com.jadebloom.goblin_api.expense.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

import com.jadebloom.goblin_api.expense.dto.CreateExpenseDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.entity.ExpenseEntity;
import com.jadebloom.goblin_api.expense_category.repository.ExpenseCategoryRepository;

@Component
public class ExpenseMapper {

	private final ModelMapper modelMapper;

	public ExpenseMapper(ModelMapper modelMapper, ExpenseCategoryRepository expenseCategoryRepository) {
		this.modelMapper = modelMapper;
	}

	public ExpenseEntity map(CreateExpenseDto createExpenseDto) {
		TypeMap<CreateExpenseDto, ExpenseEntity> typeMap = modelMapper.getTypeMap(
				CreateExpenseDto.class,
				ExpenseEntity.class);

		if (typeMap == null) {
			typeMap = modelMapper.emptyTypeMap(
					CreateExpenseDto.class,
					ExpenseEntity.class);

			typeMap.addMappings(mapper -> mapper.skip(ExpenseEntity::setId));
			typeMap.addMapping(CreateExpenseDto::getName, ExpenseEntity::setName);
			typeMap.addMapping(CreateExpenseDto::getDescription, ExpenseEntity::setDescription);
			typeMap.addMapping(CreateExpenseDto::getAmount, ExpenseEntity::setAmount);
			typeMap.addMapping(CreateExpenseDto::getCurrencyCode, ExpenseEntity::setCurrencyCode);
			typeMap.addMapping(CreateExpenseDto::getLabels, ExpenseEntity::setLabels);
			typeMap.addMappings(mapper -> mapper.skip(ExpenseEntity::setExpenseCategory));
		}

		return typeMap.map(createExpenseDto);
	}

	public ExpenseDto map(ExpenseEntity expenseEntity) {
		TypeMap<ExpenseEntity, ExpenseDto> typeMap = modelMapper.getTypeMap(
				ExpenseEntity.class,
				ExpenseDto.class);

		if (typeMap == null) {
			typeMap = modelMapper.createTypeMap(
					ExpenseEntity.class,
					ExpenseDto.class);

			typeMap.addMapping(ExpenseEntity::getId, ExpenseDto::setId);
			typeMap.addMapping(ExpenseEntity::getName, ExpenseDto::setName);
			typeMap.addMapping(ExpenseEntity::getDescription, ExpenseDto::setDescription);
			typeMap.addMapping(ExpenseEntity::getAmount, ExpenseDto::setAmount);
			typeMap.addMapping(ExpenseEntity::getCurrencyCode, ExpenseDto::setCurrencyCode);
			typeMap.addMapping(ExpenseEntity::getLabels, ExpenseDto::setLabels);
			typeMap.addMappings(mapper -> mapper.map(
					src -> src.getExpenseCategory().getId(), ExpenseDto::setExpenseCategoryId));
			typeMap.addMappings(mapper -> mapper.map(
					src -> src.getExpenseCategory().getName(), ExpenseDto::setExpenseCategoryName));
			typeMap.addMappings(mapper -> mapper.map(
					src -> src.getCreator().getId(), ExpenseDto::setCreatorId));
		}

		return typeMap.map(expenseEntity);
	}

}
