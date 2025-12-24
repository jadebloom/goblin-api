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

import com.jadebloom.goblin_api.expense.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.UpdateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.service.ExpenseCategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/expenses/categories")
public class ExpenseCategoryController {

    private final ExpenseCategoryService expenseCategoryService;

    public ExpenseCategoryController(ExpenseCategoryService expenseCategoryService) {
        this.expenseCategoryService = expenseCategoryService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<ExpenseCategoryDto> createExpenseCategory(
            @Valid @RequestBody CreateExpenseCategoryDto createDto) {
        ExpenseCategoryDto result = expenseCategoryService.create(createDto);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<Page<ExpenseCategoryDto>> findExpenseCategories(Pageable pageable) {
        Page<ExpenseCategoryDto> page = expenseCategoryService.findAuthenticatedUserExpenseCategories(
                pageable);

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseCategoryDto> findExpenseCategoryById(
            @PathVariable(name = "id") Long expenseCategoryId) {
        ExpenseCategoryDto dto = expenseCategoryService.findById(expenseCategoryId);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping
    public ResponseEntity<ExpenseCategoryDto> updateExpenseCategory(
            @Valid @RequestBody UpdateExpenseCategoryDto updateDto) {
        ExpenseCategoryDto result = expenseCategoryService.update(updateDto);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpenseCategoryById(
            @PathVariable(name = "id") Long expenseCategoryId) {
        expenseCategoryService.deleteById(expenseCategoryId);

        return ResponseEntity.noContent().build();
    }

}
