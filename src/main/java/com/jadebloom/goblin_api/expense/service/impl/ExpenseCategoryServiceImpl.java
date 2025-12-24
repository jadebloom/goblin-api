package com.jadebloom.goblin_api.expense.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.currency.error.CurrencyInUseException;
import com.jadebloom.goblin_api.expense.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.UpdateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNameUnavailableException;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.mapper.ExpenseCategoryMapper;
import com.jadebloom.goblin_api.expense.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.expense.repository.ExpenseRepository;
import com.jadebloom.goblin_api.expense.service.ExpenseCategoryService;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.repository.UserRepository;
import com.jadebloom.goblin_api.security.util.SecurityContextUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

@Service
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

    private final ExpenseCategoryRepository expenseCategoryRepository;

    private final UserRepository userRepository;

    private final ExpenseRepository expenseRepository;

    private final ExpenseCategoryMapper mapper;

    public ExpenseCategoryServiceImpl(
            ExpenseCategoryRepository expenseCategoryRepository,
            UserRepository userRepository,
            ExpenseRepository expenseRepository,
            ExpenseCategoryMapper mapper) {
        this.expenseCategoryRepository = expenseCategoryRepository;

        this.userRepository = userRepository;

        this.expenseRepository = expenseRepository;

        this.mapper = mapper;
    }

    @Override
    public ExpenseCategoryDto create(CreateExpenseCategoryDto createDto)
            throws ForbiddenException, ExpenseCategoryNameUnavailableException {
        Optional<String> optCreatorEmail = SecurityContextUtils.getAuthenticatedUserEmail();
        if (optCreatorEmail.isEmpty()) {
            throw new ForbiddenException();
        }
        String creatorEmail = optCreatorEmail.get();

        Optional<UserEntity> optCreator = userRepository.findByEmail(creatorEmail);
        if (optCreator.isEmpty()) {
            throw new ForbiddenException();
        }
        UserEntity creator = optCreator.get();

        String name = createDto.getName();
        if (expenseCategoryRepository.existsByName(name)) {
            String f = "Expense category with the name '%s' already exists";
            String errorMessage = String.format(f, name);

            throw new ExpenseCategoryNameUnavailableException(errorMessage);
        }

        ExpenseCategoryEntity created = mapper.map(createDto);
        created.setCreator(creator);

        return mapper.map(expenseCategoryRepository.save(created));
    }

    @Override
    public Page<ExpenseCategoryDto> findAuthenticatedUserExpenseCategories(Pageable pageable)
            throws ForbiddenException {
        Optional<String> optCreatorEmail = SecurityContextUtils.getAuthenticatedUserEmail();
        if (optCreatorEmail.isEmpty()) {
            throw new ForbiddenException();
        }
        String creatorEmail = optCreatorEmail.get();

        Page<ExpenseCategoryEntity> page = expenseCategoryRepository.findAllByCreator_Email(
                creatorEmail, pageable);

        return page.map(mapper::map);
    }

    @Override
    public ExpenseCategoryDto findById(Long expenseCategoryId)
            throws ForbiddenException, ExpenseCategoryNotFoundException {
        Optional<String> optCreatorEmail = SecurityContextUtils.getAuthenticatedUserEmail();
        if (optCreatorEmail.isEmpty()) {
            throw new ForbiddenException();
        }
        String creatorEmail = optCreatorEmail.get();

        Optional<ExpenseCategoryEntity> optExpenseCategory = expenseCategoryRepository.findById(
                expenseCategoryId);
        if (optExpenseCategory.isEmpty()) {
            String f = "Expense category with the ID '%d' wasn't found";

            throw new ExpenseCategoryNotFoundException(String.format(f, expenseCategoryId));
        }

        ExpenseCategoryEntity expenseCategory = optExpenseCategory.get();
        if (!expenseCategory.getCreator().getEmail().equals(creatorEmail)) {
            throw new ForbiddenException();
        }

        return mapper.map(expenseCategory);
    }

    @Override
    public boolean existsById(Long expenseCategoryId) throws ForbiddenException {
        Optional<String> optCreatorEmail = SecurityContextUtils.getAuthenticatedUserEmail();
        if (optCreatorEmail.isEmpty()) {
            throw new ForbiddenException();
        }
        String creatorEmail = optCreatorEmail.get();

        return expenseCategoryRepository.existsByIdAndCreator_Email(
                expenseCategoryId, creatorEmail);
    }

    @Override
    public ExpenseCategoryDto update(UpdateExpenseCategoryDto updateDto)
            throws ForbiddenException,
            ExpenseCategoryNameUnavailableException,
            ExpenseCategoryNotFoundException {
        Optional<String> optCreatorEmail = SecurityContextUtils.getAuthenticatedUserEmail();
        if (optCreatorEmail.isEmpty()) {
            throw new ForbiddenException();
        }
        String creatorEmail = optCreatorEmail.get();

        Optional<UserEntity> optCreator = userRepository.findByEmail(creatorEmail);
        if (optCreator.isEmpty()) {
            throw new ForbiddenException();
        }
        UserEntity creator = optCreator.get();

        Optional<ExpenseCategoryEntity> optExpenseCategory = expenseCategoryRepository.findById(
                updateDto.getId());
        if (optExpenseCategory.isEmpty()) {
            String f = "Expense category with the ID '%d' doesn't exist";

            throw new ExpenseCategoryNotFoundException(String.format(f, updateDto.getId()));
        }

        ExpenseCategoryEntity expenseCategory = optExpenseCategory.get();
        if (expenseCategoryRepository.existsByIdNotAndName(
                expenseCategory.getId(), updateDto.getName())) {
            String f = "Expense category with the name '%s' already exists";
            String errorMessage = String.format(f, updateDto.getName());

            throw new ExpenseCategoryNameUnavailableException(errorMessage);
        }

        expenseCategory.setName(updateDto.getName());
        expenseCategory.setDescription(updateDto.getDescription());
        expenseCategory.setCreator(creator);

        return mapper.map(expenseCategoryRepository.save(expenseCategory));
    }

    @Override
    public void deleteById(Long expenseCategoryId) throws CurrencyInUseException {
        Optional<String> optUserEmail = SecurityContextUtils.getAuthenticatedUserEmail();
        if (optUserEmail.isEmpty()) {
            throw new ForbiddenException();
        }

        String userEmail = optUserEmail.get();
        if (!expenseCategoryRepository.existsByIdAndCreator_Email(expenseCategoryId, userEmail)) {
            return;
        }

        if (expenseRepository.existsByExpenseCategory_Id(expenseCategoryId)) {
            String f = "Cannot delete the expense category with the ID '%d': some amount of expenses depend use it";
            String errorMessage = String.format(f, expenseCategoryId);

            throw new CurrencyInUseException(errorMessage);
        }

        expenseCategoryRepository.deleteById(expenseCategoryId);
    }

}
