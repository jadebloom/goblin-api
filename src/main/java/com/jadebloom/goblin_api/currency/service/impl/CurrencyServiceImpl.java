package com.jadebloom.goblin_api.currency.service.impl;

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
	public CurrencyDto create(CreateCurrencyDto createDto)
			throws ForbiddenException,
			InvalidCurrencyException,
			CurrencyNameUnavailableException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new ForbiddenException());

		if (!GenericValidator.isValid(createDto)) {
			String message = GenericValidator.getValidationErrorMessage(createDto);

			throw new InvalidCurrencyException(message);
		}

		if (currencyRepository.existsByName(createDto.getName())) {
			String f = "Currency with the name '%s' already exists";
			String errorMessage = String.format(f, createDto.getName());

			throw new CurrencyNameUnavailableException(errorMessage);
		}

		CurrencyEntity toCreate = mapper.map(createDto);
		toCreate.setCreator(user);

		return mapper.map(currencyRepository.saveAndFlush(toCreate));
	}

	@Override
	public Page<CurrencyDto> findAuthenticatedUserCurrencies(Pageable pageable)
			throws ForbiddenException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		Page<CurrencyEntity> page = currencyRepository.findAllByCreator_Id(userId, pageable);

		return page.map(mapper::map);
	}

	@Override
	public CurrencyDto findById(Long currencyId)
			throws ForbiddenException, CurrencyNotFoundException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		CurrencyEntity currency = currencyRepository.findById(currencyId)
				.orElseThrow(() -> {
					String f = "Currency with the ID '%d' wasn't found";

					throw new CurrencyNotFoundException(String.format(f, currencyId));
				});

		if (!currency.getCreator().getId().equals(userId)) {
			throw new ForbiddenException();
		}

		return mapper.map(currency);
	}

	@Override
	public CurrencyDto update(UpdateCurrencyDto updateDto)
			throws ForbiddenException,
			InvalidCurrencyException,
			CurrencyNotFoundException,
			CurrencyNameUnavailableException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		if (!GenericValidator.isValid(updateDto)) {
			String message = GenericValidator.getValidationErrorMessage(updateDto);

			throw new InvalidCurrencyException(message);
		}

		CurrencyEntity currency = currencyRepository.findById(updateDto.getId())
				.orElseThrow(() -> {
					String f = "Currency with the ID '%d' doesn't exist";

					throw new CurrencyNotFoundException(String.format(f, updateDto.getId()));
				});

		if (currency.getCreator().getId() != userId) {
			throw new ForbiddenException();
		}

		if (currencyRepository.existsByIdNotAndName(currency.getId(), updateDto.getName())) {
			String f = "Currency with the name '%s' already exists";
			String errorMessage = String.format(f, updateDto.getName());

			throw new CurrencyNameUnavailableException(errorMessage);
		}

		currency.setName(updateDto.getName());
		currency.setAlphabeticalCode(updateDto.getAlphabeticalCode());

		return mapper.map(currencyRepository.saveAndFlush(currency));
	}

	@Override
	public void deleteById(Long currencyId)
			throws ForbiddenException, CurrencyNotFoundException, CurrencyInUseException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		CurrencyEntity currency = currencyRepository.findById(currencyId)
				.orElseThrow(() -> {
					String f = "Currency with the ID '%d' doesn't exist";

					throw new CurrencyNotFoundException(String.format(f, currencyId));
				});

		if (currency.getCreator().getId() != userId) {
			throw new ForbiddenException();
		}

		if (expenseRepository.existsByCurrency_Id(currencyId)) {
			String f = "Cannot delete the currency with the ID '%d': some amount of expenses depend use it";
			String errorMessage = String.format(f, currencyId);

			throw new CurrencyInUseException(errorMessage);
		}

		currencyRepository.deleteById(currencyId);
	}

}
