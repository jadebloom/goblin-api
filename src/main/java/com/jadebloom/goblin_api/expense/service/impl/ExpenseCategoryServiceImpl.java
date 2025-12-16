package com.jadebloom.goblin_api.expense.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.expense.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.UpdateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNameUnavailableException;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.mapper.ExpenseCategoryMapper;
import com.jadebloom.goblin_api.expense.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.expense.service.ExpenseCategoryService;

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
    public ExpenseCategoryDto create(CreateExpenseCategoryDto createDto)
            throws ExpenseCategoryNameUnavailableException {
        String name = createDto.getName();

        if (expenseCategoryRepository.existsByName(name)) {
            String f = "Expense category with the name '%s' already exists";
            String errorMessage = String.format(f, name);

            throw new ExpenseCategoryNameUnavailableException(errorMessage);
        }

        ExpenseCategoryEntity created = mapper.map(createDto);

        return mapper.map(expenseCategoryRepository.save(created));
    }

    @Override
    public Page<ExpenseCategoryDto> findAll(Pageable pageable) {
        Page<ExpenseCategoryEntity> page = expenseCategoryRepository.findAll(pageable);

        return page.map(mapper::map);
    }

    @Override
    public ExpenseCategoryDto findById(Long expenseCategoryId)
            throws ExpenseCategoryNotFoundException {
        Optional<ExpenseCategoryEntity> found = expenseCategoryRepository.findById(expenseCategoryId);

        if (found.isEmpty()) {
            String f = "Expense category with the ID '%d' wasn't found";

            throw new ExpenseCategoryNotFoundException(String.format(f, expenseCategoryId));
        }

        return mapper.map(found.get());
    }

    @Override
    public boolean existsById(Long expenseCategoryId) {
        return expenseCategoryRepository.existsById(expenseCategoryId);
    }

    @Override
    public ExpenseCategoryDto update(UpdateExpenseCategoryDto updateDto)
            throws ExpenseCategoryNotFoundException,
            ExpenseCategoryNameUnavailableException {
        Optional<ExpenseCategoryEntity> optional = expenseCategoryRepository.findById(
                updateDto.getId());

        if (optional.isEmpty()) {
            String f = "Expense category with the ID '%d' doesn't exist";

            throw new ExpenseCategoryNotFoundException(String.format(f, updateDto.getId()));
        }

        ExpenseCategoryEntity entity = optional.get();

        if (expenseCategoryRepository.existsByIdNotAndName(entity.getId(), updateDto.getName())) {
            String f = "Expense category with the name '%s' already exists";
            String errorMessage = String.format(f, updateDto.getName());

            throw new ExpenseCategoryNameUnavailableException(errorMessage);
        }

        entity.setName(updateDto.getName());
        entity.setDescription(updateDto.getDescription());

        return mapper.map(expenseCategoryRepository.save(entity));
    }

    @Override
    public void deleteById(Long expenseCategoryId) {
        expenseCategoryRepository.deleteById(expenseCategoryId);
    }

}
