package com.jadebloom.goblin_api.expense.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jadebloom.goblin_api.expense.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.service.ExpenseCategoryService;

@RestController
@RequestMapping("/api/v1/expenses/categories")
public class ExpenseCategoryController {

    private final ExpenseCategoryService expenseCategoryService;

    public ExpenseCategoryController(
            @Qualifier("expenseCategoryServiceImpl") ExpenseCategoryService expenseCategoryService) {
        this.expenseCategoryService = expenseCategoryService;
    }

    @PostMapping
    public ResponseEntity<ExpenseCategoryDto> createExpenseCategory(
            @RequestBody ExpenseCategoryDto expenseCategoryDto) {
        ExpenseCategoryDto result = expenseCategoryService.save(expenseCategoryDto);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<ExpenseCategoryDto>> findExpenseCategories(Pageable pageable) {
        Page<ExpenseCategoryDto> page = expenseCategoryService.findAll(pageable);

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseCategoryDto> findExpenseCategoryById(
            @PathVariable(name = "id") Long expenseCategoryId) {
        ExpenseCategoryDto dto = expenseCategoryService.findById(expenseCategoryId);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<ExpenseCategoryDto> fullUpdateExpenseCategory(
            @RequestBody ExpenseCategoryDto expenseCategoryDto) {
        ExpenseCategoryDto result = expenseCategoryService.save(expenseCategoryDto);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllExpenseCategories() {
        expenseCategoryService.deleteAll();

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpenseCategoryById(
            @PathVariable(name = "id") Long expenseCategoryId) {
        expenseCategoryService.deleteById(expenseCategoryId);

        return ResponseEntity.noContent().build();
    }

}
