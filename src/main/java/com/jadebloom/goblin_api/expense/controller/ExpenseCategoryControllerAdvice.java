package com.jadebloom.goblin_api.expense.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jadebloom.goblin_api.expense.error.ExpenseCategoryInUseException;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNameUnavailableException;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseCategoryException;

@ControllerAdvice
public class ExpenseCategoryControllerAdvice {

	private final String API_DOCS_URI;

	public ExpenseCategoryControllerAdvice(@Value("${api.docs.uri}") String API_DOCS_URI) {
		this.API_DOCS_URI = API_DOCS_URI;
	}

	@ExceptionHandler(InvalidExpenseCategoryException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleInvalidExpenseCategoryException(InvalidExpenseCategoryException ex) {
		ErrorResponse errorResponse = ErrorResponse
				.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage())
				.type(URI.create(API_DOCS_URI))
				.title("Invalid expense category")
				.build();

		return errorResponse;
	}

	@ExceptionHandler(ExpenseCategoryNameUnavailableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleExpenseCategoryNameUnavailableException(
			ExpenseCategoryNameUnavailableException ex) {
		ErrorResponse errorResponse = ErrorResponse
				.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage())
				.type(URI.create(API_DOCS_URI))
				.title("Expense category name unavailable")
				.build();

		return errorResponse;
	}

	@ExceptionHandler(ExpenseCategoryNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleExpenseCategoryNotFoundException(ExpenseCategoryNotFoundException ex) {
		ErrorResponse errorResponse = ErrorResponse
				.builder(ex, HttpStatus.NOT_FOUND, ex.getMessage())
				.type(URI.create(API_DOCS_URI))
				.title("Expense category not found")
				.build();

		return errorResponse;
	}

	@ExceptionHandler(ExpenseCategoryInUseException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleCurrencyInUseException(ExpenseCategoryInUseException ex) {
		ErrorResponse errorResponse = ErrorResponse
				.builder(ex, HttpStatus.CONFLICT, ex.getMessage())
				.type(URI.create(API_DOCS_URI))
				.title("Expense category is in use")
				.build();

		return errorResponse;
	}

}
