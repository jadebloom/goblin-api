package com.jadebloom.goblin_api.expense.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;

@RestControllerAdvice
public class ExpenseControllerAdvice {

    private final String API_DOCS_URI;

    public ExpenseControllerAdvice(@Value("${api.docs.uri}") String API_DOCS_URI) {
        this.API_DOCS_URI = API_DOCS_URI;
    }

    @ExceptionHandler(ExpenseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleExpenseNotFoundException(
            ExpenseNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse
                .builder(ex, HttpStatus.NOT_FOUND, ex.getMessage())
                .type(URI.create(API_DOCS_URI))
                .title("Expense not found")
                .build();

        return errorResponse;
    }

}
