package com.jadebloom.goblin_api.currency.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jadebloom.goblin_api.currency.dto.CreateCurrencyDto;
import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.dto.UpdateCurrencyDto;
import com.jadebloom.goblin_api.currency.service.CurrencyService;
import com.jadebloom.goblin_api.expense.service.ExpenseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/currencies")
public class CurrencyController {

	private final CurrencyService currencyService;

	private final ExpenseService expenseService;

	public CurrencyController(CurrencyService currencyService, ExpenseService expenseService) {
		this.currencyService = currencyService;

		this.expenseService = expenseService;
	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping()
	public ResponseEntity<CurrencyDto> createCurrency(
			@Valid @RequestBody CreateCurrencyDto createDto) {
		CurrencyDto created = currencyService.create(createDto);

		return new ResponseEntity<>(created, HttpStatus.CREATED);
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping()
	public ResponseEntity<Page<CurrencyDto>> findAuthenticatedUserCurrencies(Pageable pageable) {
		Page<CurrencyDto> page = currencyService.findAuthenticatedUserCurrencies(pageable);

		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping(value = "/{id}")
	public ResponseEntity<CurrencyDto> findCurrencyById(
			@PathVariable(name = "id") Long currencyId) {
		CurrencyDto found = currencyService.findById(currencyId);

		return new ResponseEntity<>(found, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping(value = "/{id}")
	public ResponseEntity<CurrencyDto> updateCurrency(@PathVariable(name = "id") Long currencyId,
			@Valid @RequestBody UpdateCurrencyDto updateDto) {
		CurrencyDto updated = currencyService.update(currencyId, updateDto);

		return new ResponseEntity<>(updated, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/all")
	public ResponseEntity<Void> deleteAll() {
		currencyService.deleteAll();

		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/{id}/expenses")
	public ResponseEntity<Void> deleteAllExpensesByCurrencyId(
			@PathVariable(name = "id") Long expenseCategoryId) {
		expenseService.deleteAllExpensesByCurrencyId(expenseCategoryId);

		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCurrencyById(@PathVariable(name = "id") Long currencyId) {
		currencyService.deleteById(currencyId);

		return ResponseEntity.noContent().build();
	}

}
