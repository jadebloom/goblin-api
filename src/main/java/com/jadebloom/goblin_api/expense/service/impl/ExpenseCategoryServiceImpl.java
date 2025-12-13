package com.jadebloom.goblin_api.expense.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.expense.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNameUnavailableException;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseCategoryException;
import com.jadebloom.goblin_api.expense.mapper.ExpenseCategoryMapper;
import com.jadebloom.goblin_api.expense.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.expense.service.ExpenseCategoryService;
import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@Service
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

    private final ExpenseCategoryRepository expenseCategoryRepository;

    private final ExpenseCategoryMapper mapper;

    public ExpenseCategoryServiceImpl(
            ExpenseCategoryRepository expenseCategoryRepository,
            ExpenseCategoryMapper mapper) {
        this.expenseCategoryRepository = expenseCategoryRepository;

        this.mapper = mapper;
    }

    @Override
    public ExpenseCategoryDto create(CreateExpenseCategoryDto createExpenseCategoryDto)
            throws InvalidExpenseCategoryException, ExpenseCategoryNameUnavailableException {
        if (!GenericValidator.isValid(createExpenseCategoryDto)) {
            String message = GenericValidator.getValidationErrorMessage(createExpenseCategoryDto);

            throw new InvalidExpenseCategoryException(message);
        }

        if (expenseCategoryRepository.existsByName(createExpenseCategoryDto.getName())) {
            String f = "Expense category with name=%s already exists";
            String errorMessage = String.format(f, createExpenseCategoryDto.getName());

            throw new ExpenseCategoryNameUnavailableException(errorMessage);
        }

        ExpenseCategoryEntity entity = mapper.map(createExpenseCategoryDto);
        ExpenseCategoryEntity savedEntity = expenseCategoryRepository.save(entity);

        return mapper.map(savedEntity);
    }

    @Override
    public Page<ExpenseCategoryDto> findAll(Pageable pageable) {
        Page<ExpenseCategoryEntity> page = expenseCategoryRepository.findAll(pageable);

        return page.map(mapper::map);
    }

    @Override
    public ExpenseCategoryDto findById(Long expenseCategoryId) throws ExpenseCategoryNotFoundException {
        Optional<ExpenseCategoryEntity> entity = expenseCategoryRepository.findById(expenseCategoryId);

        if (entity.isEmpty()) {
            String f = "Expense category with ID=%d wasn't found";

            throw new ExpenseCategoryNotFoundException(String.format(f, expenseCategoryId));
        }

        return mapper.map(entity.get());
    }

    @Override
    public boolean existsById(Long expenseCategoryId) {
        return expenseCategoryRepository.existsById(expenseCategoryId);
    }

    @Override
    public boolean existsByName(String expenseCategoryName) {
        return expenseCategoryRepository.existsByName(expenseCategoryName);
    }

    @Override
    public boolean existsByIdNotAndName(Long expenseCategoryId, String expenseCategoryName) {
        return expenseCategoryRepository.existsByIdNotAndName(expenseCategoryId, expenseCategoryName);
    }

    @Override
    public ExpenseCategoryDto update(ExpenseCategoryDto expenseCategoryDto)
            throws InvalidExpenseCategoryException,
            ExpenseCategoryNotFoundException,
            ExpenseCategoryNameUnavailableException {
        if (!GenericValidator.isValid(expenseCategoryDto)) {
            String message = GenericValidator.getValidationErrorMessage(expenseCategoryDto);

            throw new InvalidExpenseCategoryException(message);
        }

        Long id = expenseCategoryDto.getId();
        String name = expenseCategoryDto.getName();

        if (!existsById(id)) {
            String f = "Expense category with ID=%d doesn't exist";

            throw new ExpenseCategoryNotFoundException(String.format(f, id));
        }

        if (expenseCategoryRepository.existsByIdNotAndName(id, name)) {
            String f = "Expense category with name=%s already exists";
            String errorMessage = String.format(f, name);

            throw new ExpenseCategoryNameUnavailableException(errorMessage);
        }

        ExpenseCategoryEntity entity = mapper.map(expenseCategoryDto);

        return mapper.map(expenseCategoryRepository.save(entity));
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
