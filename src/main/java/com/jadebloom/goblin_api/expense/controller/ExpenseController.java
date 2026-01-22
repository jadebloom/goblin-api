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
import com.jadebloom.goblin_api.expense.dto.DeleteExpensesDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.dto.UpdateExpenseDto;
import com.jadebloom.goblin_api.expense.service.ExpenseCreateService;
import com.jadebloom.goblin_api.expense.service.ExpenseDeleteService;
import com.jadebloom.goblin_api.expense.service.ExpenseFindService;
import com.jadebloom.goblin_api.expense.service.ExpenseUpdateService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/expenses")
public class ExpenseController {

	private final ExpenseCreateService createService;

	private final ExpenseFindService findService;

	private final ExpenseUpdateService updateService;

	private final ExpenseDeleteService deleteService;

	public ExpenseController(ExpenseCreateService createService,
			ExpenseFindService findService,
			ExpenseUpdateService updateService,
			ExpenseDeleteService deleteService) {
		this.createService = createService;

		this.findService = findService;

		this.updateService = updateService;

		this.deleteService = deleteService;
	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping
	public ResponseEntity<ExpenseDto> createExpense(
			@Valid @RequestBody CreateExpenseDto createDto) {
		ExpenseDto dto = createService.create(createDto);

		return new ResponseEntity<>(dto, HttpStatus.CREATED);
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping
	public ResponseEntity<Page<ExpenseDto>> findAuthenticatedUserExpenses(Pageable pageable) {
		Page<ExpenseDto> page = findService.findUserAuthenticatedExpenses(pageable);

		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/{id}")
	public ResponseEntity<ExpenseDto> findExpenseById(
			@PathVariable(name = "id") Long expenseId) {
		ExpenseDto dto = findService.findById(expenseId);

		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping("/{id}")
	public ResponseEntity<ExpenseDto> updateExpenseById(
			@PathVariable(name = "id") Long expenseId,
			@Valid @RequestBody UpdateExpenseDto updateDto) {
		ExpenseDto result = updateService.update(expenseId, updateDto);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/all")
	public ResponseEntity<Void> deleteAllExpenses() {
		deleteService.deleteAll();

		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping("/delete")
	public ResponseEntity<Void> deleteSelectedExpensesById(
			@Valid @RequestBody DeleteExpensesDto deleteDto) {
		deleteService.deleteSelectedById(deleteDto);

		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteExpenseById(
			@PathVariable(name = "id") Long expenseId) {
		deleteService.deleteById(expenseId);

		return ResponseEntity.noContent().build();
	}

}
