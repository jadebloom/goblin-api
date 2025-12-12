package com.jadebloom.goblin_api.expense.controller;

import java.net.URI;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseException;
import com.jadebloom.goblin_api.shared.util.Links;

@RestControllerAdvice
public class ExpenseControllerAdvice {

    @ExceptionHandler(ExpenseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleExpenseNotFoundException(
            ExpenseNotFoundException ex, WebRequest req) {
        ErrorResponse errorResponse = ErrorResponse.builder(ex, HttpStatus.NOT_FOUND, ex.getMessage())
                .type(URI.create(Links.API_DOCS_URI))
                .title("Expense not found")
                .instance(URI.create(req.getContextPath()))
                .property("timestamp", Instant.now())
                .build();

        return errorResponse;
    }

    @ExceptionHandler(InvalidExpenseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidExpenseException(
            InvalidExpenseException ex, WebRequest req) {
        ErrorResponse errorResponse = ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage())
                .type(URI.create(Links.API_DOCS_URI))
                .title("Invalid expense")
                .instance(URI.create(req.getContextPath()))
                .property("timestamp", Instant.now())
                .build();

        return errorResponse;
    }

}
