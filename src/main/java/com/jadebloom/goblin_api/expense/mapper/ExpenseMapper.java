package com.jadebloom.goblin_api.expense.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;
import com.jadebloom.goblin_api.currency.repository.CurrencyRepository;
import com.jadebloom.goblin_api.expense.dto.CreateExpenseDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.dto.UpdateExpenseDto;
import com.jadebloom.goblin_api.expense.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense.entity.ExpenseEntity;
import com.jadebloom.goblin_api.expense.repository.ExpenseCategoryRepository;

@Component
public class ExpenseMapper {

    private final ModelMapper modelMapper;

    private final ExpenseCategoryRepository expenseCategoryRepository;

    private final CurrencyRepository currencyRepository;

    public ExpenseMapper(
            ModelMapper modelMapper,
            ExpenseCategoryRepository expenseCategoryRepository,
            CurrencyRepository currencyRepository) {
        this.modelMapper = modelMapper;

        this.expenseCategoryRepository = expenseCategoryRepository;

        this.currencyRepository = currencyRepository;
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
            typeMap.addMapping(CreateExpenseDto::getLabels, ExpenseEntity::setLabels);

            typeMap.addMappings(mapper -> mapper.<ExpenseCategoryEntity>map(src -> {
                Long id = createExpenseDto.getExpenseCategoryId();
                ExpenseCategoryEntity e = expenseCategoryRepository.findById(id).orElse(null);

                return e;
            }, ExpenseEntity::setExpenseCategory));
            typeMap.addMappings(mapper -> mapper.<CurrencyEntity>map(src -> {
                Long id = createExpenseDto.getCurrencyId();
                CurrencyEntity e = currencyRepository.findById(id).orElse(null);

                return e;
            }, ExpenseEntity::setCurrency));
        }

        return typeMap.map(createExpenseDto);
    }

    public ExpenseEntity map(UpdateExpenseDto updateDto) {
        TypeMap<UpdateExpenseDto, ExpenseEntity> typeMap = modelMapper.getTypeMap(
                UpdateExpenseDto.class,
                ExpenseEntity.class);

        if (typeMap == null) {
            typeMap = modelMapper.emptyTypeMap(
                    UpdateExpenseDto.class,
                    ExpenseEntity.class);

            typeMap.addMapping(UpdateExpenseDto::getId, ExpenseEntity::setId);
            typeMap.addMapping(UpdateExpenseDto::getName, ExpenseEntity::setName);
            typeMap.addMapping(UpdateExpenseDto::getDescription, ExpenseEntity::setDescription);
            typeMap.addMapping(UpdateExpenseDto::getAmount, ExpenseEntity::setAmount);
            typeMap.addMapping(UpdateExpenseDto::getLabels, ExpenseEntity::setLabels);

            typeMap.addMappings(mapper -> mapper.<ExpenseCategoryEntity>map(src -> {
                Long id = updateDto.getExpenseCategoryId();
                ExpenseCategoryEntity e = expenseCategoryRepository.findById(id).orElse(null);

                return e;
            }, ExpenseEntity::setExpenseCategory));
            typeMap.addMappings(mapper -> mapper.<CurrencyEntity>map(src -> {
                Long id = updateDto.getCurrencyId();
                CurrencyEntity e = currencyRepository.findById(id).orElse(null);

                return e;
            }, ExpenseEntity::setCurrency));
        }

        return typeMap.map(updateDto);
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
            typeMap.addMapping(ExpenseEntity::getLabels, ExpenseDto::setLabels);
            typeMap.addMappings(mapper -> mapper.map(
                    src -> src.getExpenseCategory().getId(), ExpenseDto::setExpenseCategoryId));
            typeMap.addMappings(mapper -> mapper.map(
                    src -> src.getCurrency().getId(), ExpenseDto::setCurrencyId));
        }

        return typeMap.map(expenseEntity);
    }

}
