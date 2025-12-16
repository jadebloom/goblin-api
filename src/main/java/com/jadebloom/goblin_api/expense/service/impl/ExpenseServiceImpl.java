package com.jadebloom.goblin_api.expense.service.impl;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.currency.repository.CurrencyRepository;
import com.jadebloom.goblin_api.expense.dto.CreateExpenseDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.dto.UpdateExpenseDto;
import com.jadebloom.goblin_api.expense.entity.ExpenseEntity;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.error.ExpenseNameUnavailableException;
import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;
import com.jadebloom.goblin_api.expense.mapper.ExpenseMapper;
import com.jadebloom.goblin_api.expense.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.expense.repository.ExpenseRepository;
import com.jadebloom.goblin_api.expense.service.ExpenseService;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    private final ExpenseCategoryRepository expenseCategoryRepository;

    private final CurrencyRepository currencyRepository;

    private final ExpenseMapper mapper;

    public ExpenseServiceImpl(
            ExpenseRepository expenseRepository,
            ExpenseCategoryRepository expenseCategoryRepository,
            CurrencyRepository currencyRepository,
            ExpenseMapper mapper) {
        this.expenseRepository = expenseRepository;

        this.expenseCategoryRepository = expenseCategoryRepository;

        this.currencyRepository = currencyRepository;

        this.mapper = mapper;
    }

    @Override
    public ExpenseDto create(CreateExpenseDto createDto)
            throws ExpenseNameUnavailableException,
            ExpenseCategoryNotFoundException,
            CurrencyNotFoundException {
        String expenseName = createDto.getName();
        Long expenseCategoryId = createDto.getExpenseCategoryId();
        Long currencyId = createDto.getCurrencyId();

        if (expenseRepository.existsByName(expenseName)) {
            String f = "Expense with the name '%s' already exists";
            String errorMessage = String.format(f, expenseName);

            throw new ExpenseNameUnavailableException(errorMessage);
        }

        if (!expenseCategoryRepository.existsById(expenseCategoryId)) {
            String f = "Expense category with the ID '%d' wasn't found";

            throw new ExpenseCategoryNotFoundException(String.format(f, expenseCategoryId));
        }

        if (!currencyRepository.existsById(currencyId)) {
            String f = "Currency with the ID '%d' wasn't found";

            throw new CurrencyNotFoundException(String.format(f, currencyId));
        }

        ExpenseEntity entity = mapper.map(createDto);
        ExpenseEntity created = expenseRepository.save(entity);

        return mapper.map(created);
    }

    @Override
    public Page<ExpenseDto> findAll(Pageable pageable) {
        Page<ExpenseEntity> page = expenseRepository.findAll(pageable);

        return page.map(mapper::map);
    }

    @Override
    public ExpenseDto findById(Long expenseId) throws ExpenseNotFoundException {
        Optional<ExpenseEntity> found = expenseRepository.findById(expenseId);

        if (found.isEmpty()) {
            String f = "Expense with the ID '%d' wasn't found";

            throw new ExpenseNotFoundException(String.format(f, expenseId));
        }

        return mapper.map(found.get());
    }

    @Override
    public ExpenseDto update(UpdateExpenseDto updateDto)
            throws ExpenseNotFoundException,
            ExpenseNameUnavailableException,
            ExpenseCategoryNotFoundException,
            CurrencyNotFoundException {
        Optional<ExpenseEntity> optionalExpense = expenseRepository.findById(updateDto.getId());

        if (optionalExpense.isEmpty()) {
            String f = "Expense with the ID '%d' wasn't found";

            throw new ExpenseNotFoundException(String.format(f, updateDto.getId()));
        }

        ExpenseEntity expense = optionalExpense.get();

        if (expenseRepository.existsByIdNotAndName(expense.getId(), updateDto.getName())) {
            String f = "Another expense with the name '%s' already exists";
            String errorMessage = String.format(f, updateDto.getName());

            throw new ExpenseNameUnavailableException(errorMessage);
        }

        if (!expenseCategoryRepository.existsById(updateDto.getExpenseCategoryId())) {
            String f = "Expense category with the ID '%d' wasn't found";
            String message = String.format(f, updateDto.getExpenseCategoryId());

            throw new ExpenseCategoryNotFoundException(message);
        }

        if (!currencyRepository.existsById(updateDto.getCurrencyId())) {
            String f = "Currency with the ID '%d' wasn't found";
            String message = String.format(f, f, updateDto.getCurrencyId());

            throw new CurrencyNotFoundException(message);
        }

        ZonedDateTime createdAt = expense.getCreatedAt();

        expense = mapper.map(updateDto);
        expense.setCreatedAt(createdAt);

        return mapper.map(expenseRepository.save(expense));
    }

    @Override
    public void deleteById(Long expenseId) {
        expenseRepository.deleteById(expenseId);
    }

}
