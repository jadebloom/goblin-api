package com.jadebloom.goblin_api.currency.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.currency.dto.CreateCurrencyDto;
import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.dto.UpdateCurrencyDto;
import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;
import com.jadebloom.goblin_api.currency.error.CurrencyInUseException;
import com.jadebloom.goblin_api.currency.error.CurrencyNameUnavailableException;
import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.currency.error.InvalidCurrencyException;
import com.jadebloom.goblin_api.currency.mapper.CurrencyMapper;
import com.jadebloom.goblin_api.currency.repository.CurrencyRepository;
import com.jadebloom.goblin_api.currency.service.CurrencyService;
import com.jadebloom.goblin_api.expense.repository.ExpenseRepository;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.repository.UserRepository;
import com.jadebloom.goblin_api.security.util.SecurityContextUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;
import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    private final UserRepository userRepository;

    private final ExpenseRepository expenseRepository;

    private final CurrencyMapper mapper;

    public CurrencyServiceImpl(
            CurrencyRepository currencyRepository,
            UserRepository userRepository,
            ExpenseRepository expenseRepository,
            CurrencyMapper mapper) {
        this.currencyRepository = currencyRepository;

        this.userRepository = userRepository;

        this.expenseRepository = expenseRepository;

        this.mapper = mapper;
    }

    @Override
    public CurrencyDto create(CreateCurrencyDto createDto) throws ForbiddenException, CurrencyNameUnavailableException {
        if (!GenericValidator.isValid(createDto)) {
            String message = GenericValidator.getValidationErrorMessage(createDto);

            throw new InvalidCurrencyException(message);
        }

        Optional<String> optCreatorEmail = SecurityContextUtils.getAuthenticatedUserEmail();
        if (optCreatorEmail.isEmpty()) {
            throw new ForbiddenException();
        }

        Optional<UserEntity> optCreator = userRepository.findByEmail(optCreatorEmail.get());
        if (optCreator.isEmpty()) {
            throw new ForbiddenException();
        }
        UserEntity creator = optCreator.get();

        if (currencyRepository.existsByName(createDto.getName())) {
            String f = "Currency with the name '%s' already exists";
            String errorMessage = String.format(f, createDto.getName());

            throw new CurrencyNameUnavailableException(errorMessage);
        }

        CurrencyEntity toCreate = mapper.map(createDto);
        toCreate.setCreator(creator);

        return mapper.map(currencyRepository.save(toCreate));
    }

    @Override
    public Page<CurrencyDto> findAuthenticatedUserCurrencies(Pageable pageable)
            throws ForbiddenException {
        Optional<String> optCreatorEmail = SecurityContextUtils.getAuthenticatedUserEmail();
        if (optCreatorEmail.isEmpty()) {
            throw new ForbiddenException();
        }
        String creatorEmail = optCreatorEmail.get();

        Page<CurrencyEntity> page = currencyRepository.findAllByCreator_Email(creatorEmail, pageable);

        return page.map(mapper::map);
    }

    @Override
    public CurrencyDto findById(Long currencyId)
            throws ForbiddenException, CurrencyNotFoundException {
        Optional<String> optCreatorEmail = SecurityContextUtils.getAuthenticatedUserEmail();
        if (optCreatorEmail.isEmpty()) {
            throw new ForbiddenException();
        }
        String creatorEmail = optCreatorEmail.get();

        Optional<CurrencyEntity> optCurrency = currencyRepository.findById(currencyId);
        if (optCurrency.isEmpty()) {
            String f = "Currency with the ID '%d' wasn't found";

            throw new CurrencyNotFoundException(String.format(f, currencyId));
        }
        CurrencyEntity currency = optCurrency.get();

        if (!currency.getCreator().getEmail().equals(creatorEmail)) {
            throw new ForbiddenException();
        }

        return mapper.map(currency);
    }

    @Override
    public boolean existsById(Long currencyId) throws ForbiddenException {
        Optional<String> optCreatorEmail = SecurityContextUtils.getAuthenticatedUserEmail();
        if (optCreatorEmail.isEmpty()) {
            throw new ForbiddenException();
        }
        String creatorEmail = optCreatorEmail.get();

        return currencyRepository.existsByIdAndCreator_Email(currencyId, creatorEmail);
    }

    @Override
    public CurrencyDto update(UpdateCurrencyDto updateDto)
            throws ForbiddenException, CurrencyNotFoundException, CurrencyNameUnavailableException {
        if (!GenericValidator.isValid(updateDto)) {
            String message = GenericValidator.getValidationErrorMessage(updateDto);

            throw new InvalidCurrencyException(message);
        }

        Optional<String> optCreatorEmail = SecurityContextUtils.getAuthenticatedUserEmail();
        if (optCreatorEmail.isEmpty()) {
            throw new ForbiddenException();
        }

        Optional<CurrencyEntity> optCurrency = currencyRepository.findById(updateDto.getId());
        if (optCurrency.isEmpty()) {
            String f = "Currency with the ID '%d' doesn't exist";

            throw new CurrencyNotFoundException(String.format(f, updateDto.getId()));
        }

        CurrencyEntity currency = optCurrency.get();
        if (currencyRepository.existsByIdNotAndName(currency.getId(), updateDto.getName())) {
            String f = "Other currency with name \"%s\" already exists";
            String errorMessage = String.format(f, updateDto.getName());

            throw new CurrencyNameUnavailableException(errorMessage);
        }

        currency.setName(updateDto.getName());
        currency.setAlphabeticalCode(updateDto.getAlphabeticalCode());

        return mapper.map(currencyRepository.save(currency));
    }

    @Override
    public void deleteById(Long currencyId) throws ForbiddenException, CurrencyInUseException {
        Optional<String> optUserEmail = SecurityContextUtils.getAuthenticatedUserEmail();
        if (optUserEmail.isEmpty()) {
            throw new ForbiddenException();
        }

        String userEmail = optUserEmail.get();
        if (!currencyRepository.existsByIdAndCreator_Email(currencyId, userEmail)) {
            return;
        }

        if (expenseRepository.existsByCurrency_Id(currencyId)) {
            String f = "Cannot delete the currency with the ID '%d': some amount of expenses depend use it";
            String errorMessage = String.format(f, currencyId);

            throw new CurrencyInUseException(errorMessage);
        }

        currencyRepository.deleteById(currencyId);
    }

}
