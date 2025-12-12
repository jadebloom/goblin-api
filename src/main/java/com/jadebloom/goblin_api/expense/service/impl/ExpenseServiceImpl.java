package com.jadebloom.goblin_api.expense.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.currency.service.CurrencyService;
import com.jadebloom.goblin_api.expense.dto.CreateExpenseDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.entity.ExpenseEntity;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseException;
import com.jadebloom.goblin_api.expense.mapper.ExpenseMapper;
import com.jadebloom.goblin_api.expense.repository.ExpenseRepository;
import com.jadebloom.goblin_api.expense.service.ExpenseCategoryService;
import com.jadebloom.goblin_api.expense.service.ExpenseService;
import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    private final ExpenseCategoryService expenseCategoryService;

    private final CurrencyService currencyService;

    private final ExpenseMapper mapper;

    public ExpenseServiceImpl(
            ExpenseRepository expenseRepository,
            @Qualifier("expenseCategoryServiceImpl") ExpenseCategoryService expenseCategoryService,
            @Qualifier("currencyServiceImpl") CurrencyService currencyService,
            ExpenseMapper mapper) {
        this.expenseRepository = expenseRepository;

        this.expenseCategoryService = expenseCategoryService;

        this.currencyService = currencyService;

        this.mapper = mapper;
    }

    @Override
    public ExpenseDto create(CreateExpenseDto createExpenseDto)
            throws ExpenseCategoryNotFoundException, CurrencyNotFoundException, InvalidExpenseException {
        Long expenseCategoryId = createExpenseDto.getExpenseCategoryId();
        Long currencyId = createExpenseDto.getCurrencyId();

        if (!expenseCategoryService.existsById(expenseCategoryId)) {
            String f = "Expense with expense category ID '%d' wasn't found";

            throw new ExpenseCategoryNotFoundException(String.format(f, expenseCategoryId));
        }

        if (!currencyService.existsById(currencyId)) {
            String f = "Expense with currency ID '%d' wasn't found";

            throw new CurrencyNotFoundException(String.format(f, currencyId));
        }

        if (!GenericValidator.isValid(createExpenseDto)) {
            String message = GenericValidator.getValidationErrorMessage(createExpenseDto);

            throw new InvalidExpenseException(message);
        }

        ExpenseEntity entity = mapper.map(createExpenseDto);
        ExpenseEntity savedEntity = expenseRepository.save(entity);

        return mapper.map(savedEntity);
    }

    @Override
    public Page<ExpenseDto> findAll(Pageable pageable) {
        Page<ExpenseEntity> page = expenseRepository.findAll(pageable);

        return page.map(mapper::map);
    }

    @Override
    public ExpenseDto findById(Long expenseId) throws ExpenseNotFoundException {
        Optional<ExpenseEntity> entity = expenseRepository.findById(expenseId);

        if (entity.isEmpty()) {
            String f = "Expense with ID=%d wasn't found";

            throw new ExpenseNotFoundException(String.format(f, expenseId));
        }

        return mapper.map(entity.get());
    }

    @Override
    public boolean existsById(Long expenseId) {
        return expenseRepository.existsById(expenseId);
    }

    @Override
    public ExpenseDto update(ExpenseDto expenseDto) throws InvalidExpenseException, ExpenseNotFoundException {
        Long expenseId = expenseDto.getId();

        if (!existsById(expenseId)) {
            String f = "Expense with ID=%d wasn't found";

            throw new ExpenseNotFoundException(String.format(f, expenseId));
        }

        if (!GenericValidator.isValid(expenseDto)) {
            String message = GenericValidator.getValidationErrorMessage(expenseDto);

            throw new InvalidExpenseException(message);
        }

        ExpenseEntity entity = mapper.map(expenseDto);
        ExpenseEntity savedEntity = expenseRepository.save(entity);

        return mapper.map(savedEntity);
    }

    @Override
    public void deleteAll() {
        expenseRepository.deleteAll();
    }

    @Override
    public void deleteById(Long expenseId) {
        expenseRepository.deleteById(expenseId);
    }

}
