package com.jadebloom.goblin_api.expense.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.expense.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseCategoryException;
import com.jadebloom.goblin_api.expense.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.expense.service.ExpenseCategoryService;
import com.jadebloom.goblin_api.expense.validation.ExpenseCategoryValidators;
import com.jadebloom.goblin_api.shared.mapper.Mapper;

@Service
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

    private final ExpenseCategoryRepository expenseCategoryRepository;

    private final Mapper<ExpenseCategoryDto, ExpenseCategoryEntity> mapper;

    public ExpenseCategoryServiceImpl(
            ExpenseCategoryRepository expenseCategoryRepository,
            @Qualifier("expenseCategoryMapperImpl") Mapper<ExpenseCategoryDto, ExpenseCategoryEntity> mapper) {
        this.expenseCategoryRepository = expenseCategoryRepository;

        this.mapper = mapper;
    }

    @Override
    public ExpenseCategoryDto save(ExpenseCategoryDto expenseCategoryDto)
            throws InvalidExpenseCategoryException, ExpenseCategoryNotFoundException {
        Long id = expenseCategoryDto.getId();

        if (id != null && !expenseCategoryRepository.existsById(id)) {
            String f = "Expense category with ID=%d doesn't exist";

            throw new ExpenseCategoryNotFoundException(String.format(f, id));
        }

        ExpenseCategoryValidators.validate(expenseCategoryDto);

        ExpenseCategoryEntity entity = mapper.mapTo(expenseCategoryDto);

        return mapper.mapFrom(expenseCategoryRepository.save(entity));
    }

    @Override
    public Page<ExpenseCategoryDto> findAll(Pageable pageable) {
        Page<ExpenseCategoryEntity> page = expenseCategoryRepository.findAll(pageable);

        return page.map(mapper::mapFrom);
    }

    @Override
    public ExpenseCategoryDto findById(Long expenseCategoryId) throws ExpenseCategoryNotFoundException {
        Optional<ExpenseCategoryEntity> entity = expenseCategoryRepository.findById(expenseCategoryId);

        if (entity.isEmpty()) {
            String f = "Expense category with ID=%d wasn't found";

            throw new ExpenseCategoryNotFoundException(String.format(f, expenseCategoryId));
        }

        return mapper.mapFrom(entity.get());
    }

    @Override
    public boolean existsById(Long expenseCategoryId) {
        return expenseCategoryRepository.existsById(expenseCategoryId);
    }

    @Override
    public void deleteAll() {
        expenseCategoryRepository.deleteAll();
    }

    @Override
    public void deleteById(Long expenseCategoryId) {
        expenseCategoryRepository.deleteById(expenseCategoryId);
    }

}
