package com.jadebloom.goblin_api.expense.controller;

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

import com.jadebloom.goblin_api.expense.dto.CreateExpenseDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.dto.UpdateExpenseDto;
import com.jadebloom.goblin_api.expense.service.ExpenseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/expenses")
public class ExpenseController {

	private final ExpenseService expenseService;

	public ExpenseController(ExpenseService expenseService) {
		this.expenseService = expenseService;
	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping
	public ResponseEntity<ExpenseDto> createExpense(
			@Valid @RequestBody CreateExpenseDto createDto) {
		ExpenseDto dto = expenseService.create(createDto);

		return new ResponseEntity<>(dto, HttpStatus.CREATED);
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping
	public ResponseEntity<Page<ExpenseDto>> findAuthenticatedUserExpenses(Pageable pageable) {
		Page<ExpenseDto> page = expenseService.findUserAuthenticatedExpenses(pageable);

		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/{id}")
	public ResponseEntity<ExpenseDto> findExpenseById(
			@PathVariable(name = "id") Long expenseId) {
		ExpenseDto dto = expenseService.findById(expenseId);

		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping
	public ResponseEntity<ExpenseDto> updateExpense(
			@Valid @RequestBody UpdateExpenseDto updateDto) {
		ExpenseDto result = expenseService.update(updateDto);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteExpenseById(
			@PathVariable(name = "id") Long expenseCategoryId) {
		expenseService.deleteById(expenseCategoryId);

		return ResponseEntity.noContent().build();
	}

}
