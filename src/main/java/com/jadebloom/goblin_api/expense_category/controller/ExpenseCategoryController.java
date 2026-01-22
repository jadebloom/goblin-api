package com.jadebloom.goblin_api.expense_category.controller;

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

import com.jadebloom.goblin_api.expense.service.ExpenseDeleteService;
import com.jadebloom.goblin_api.expense_category.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense_category.dto.DeleteExpenseCategoriesDto;
import com.jadebloom.goblin_api.expense_category.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense_category.dto.UpdateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense_category.service.ExpenseCategoryCreateService;
import com.jadebloom.goblin_api.expense_category.service.ExpenseCategoryDeleteService;
import com.jadebloom.goblin_api.expense_category.service.ExpenseCategoryFindService;
import com.jadebloom.goblin_api.expense_category.service.ExpenseCategoryUpdateService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/expenses/categories")
public class ExpenseCategoryController {

	private final ExpenseCategoryCreateService createService;

	private final ExpenseCategoryFindService findService;

	private final ExpenseCategoryUpdateService updateService;

	private final ExpenseCategoryDeleteService deleteService;

	private final ExpenseDeleteService expenseDeleteService;

	public ExpenseCategoryController(ExpenseCategoryCreateService createService,
			ExpenseCategoryFindService findService,
			ExpenseCategoryUpdateService updateService,
			ExpenseCategoryDeleteService deleteService,
			ExpenseDeleteService expenseDeleteService) {
		this.createService = createService;

		this.findService = findService;

		this.updateService = updateService;

		this.deleteService = deleteService;

		this.expenseDeleteService = expenseDeleteService;
	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping
	public ResponseEntity<ExpenseCategoryDto> createExpenseCategory(
			@Valid @RequestBody CreateExpenseCategoryDto createDto) {
		ExpenseCategoryDto result = createService.create(createDto);

		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping
	public ResponseEntity<Page<ExpenseCategoryDto>> findAuthenticatedUserExpenseCategories(
			Pageable pageable) {
		Page<ExpenseCategoryDto> page = findService.findAuthenticatedUserExpenseCategories(
				pageable);

		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/{id}")
	public ResponseEntity<ExpenseCategoryDto> findExpenseCategoryById(
			@PathVariable(name = "id") Long expenseCategoryId) {
		ExpenseCategoryDto dto = findService.findById(expenseCategoryId);

		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping(value = "/{id}")
	public ResponseEntity<ExpenseCategoryDto> updateExpenseCategoryById(
			@PathVariable(name = "id") Long expenseCategoryId,
			@Valid @RequestBody UpdateExpenseCategoryDto updateDto) {
		ExpenseCategoryDto result = updateService.update(expenseCategoryId, updateDto);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/all")
	public ResponseEntity<Void> deleteAllExpenseCategories() {
		deleteService.deleteAll();

		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping("/delete")
	public ResponseEntity<Void> deleteSelectedById(
			@Valid @RequestBody DeleteExpenseCategoriesDto deleteDto) {
		deleteService.deleteSelectedById(deleteDto);

		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/{id}/expenses")
	public ResponseEntity<Void> deleteAllExpensesByExpenseCategoryId(
			@PathVariable(name = "id") Long expenseCategoryId) {
		expenseDeleteService.deleteAllByExpenseCategoryId(expenseCategoryId);

		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteExpenseCategoryById(
			@PathVariable(name = "id") Long expenseCategoryId) {
		deleteService.deleteById(expenseCategoryId);

		return ResponseEntity.noContent().build();
	}

}
