package com.jadebloom.goblin_api.expense.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.expense.dto.CreateExpenseCategoryDto;
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

    private final Mapper mapper;

    public ExpenseCategoryServiceImpl(
            ExpenseCategoryRepository expenseCategoryRepository,
            Mapper mapper) {
        this.expenseCategoryRepository = expenseCategoryRepository;

        this.mapper = mapper;
    }

    @Override
    public ExpenseCategoryDto create(CreateExpenseCategoryDto createExpenseCategoryDto)
            throws InvalidExpenseCategoryException, ExpenseCategoryNotFoundException {
        ExpenseCategoryValidators.validate(createExpenseCategoryDto);

        ExpenseCategoryEntity entity = mapper.map(
                createExpenseCategoryDto,
                ExpenseCategoryEntity.class);

        ExpenseCategoryEntity savedEntity = expenseCategoryRepository.save(entity);

        return mapper.map(savedEntity, ExpenseCategoryDto.class);
    }

    @Override
    public Page<ExpenseCategoryDto> findAll(Pageable pageable) {
        Page<ExpenseCategoryEntity> page = expenseCategoryRepository.findAll(pageable);

        return page.map(e -> mapper.map(e, ExpenseCategoryDto.class));
    }

    @Override
    public ExpenseCategoryDto findById(Long expenseCategoryId) throws ExpenseCategoryNotFoundException {
        Optional<ExpenseCategoryEntity> entity = expenseCategoryRepository.findById(expenseCategoryId);

        if (entity.isEmpty()) {
            String f = "Expense category with ID=%d wasn't found";

            throw new ExpenseCategoryNotFoundException(String.format(f, expenseCategoryId));
        }

        return mapper.map(entity.get(), ExpenseCategoryDto.class);
    }

    @Override
    public boolean existsById(Long expenseCategoryId) {
        return expenseCategoryRepository.existsById(expenseCategoryId);
    }

    @Override
    public ExpenseCategoryDto update(ExpenseCategoryDto expenseCategoryDto)
            throws InvalidExpenseCategoryException, ExpenseCategoryNotFoundException {
        Long id = expenseCategoryDto.getId();

        if (!expenseCategoryRepository.existsById(id)) {
            String f = "Expense category with ID=%d doesn't exist";

            throw new ExpenseCategoryNotFoundException(String.format(f, id));
        }

        ExpenseCategoryValidators.validate(expenseCategoryDto);

        ExpenseCategoryEntity entity = mapper.map(
                expenseCategoryDto, ExpenseCategoryEntity.class);

        return mapper.map(expenseCategoryRepository.save(entity), ExpenseCategoryDto.class);
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
