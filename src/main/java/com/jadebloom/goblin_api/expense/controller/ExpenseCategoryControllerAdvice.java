package com.jadebloom.goblin_api.expense.controller;

import java.net.URI;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNameUnavailableException;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;

@ControllerAdvice
public class ExpenseCategoryControllerAdvice {

	private final String API_DOCS_URI;

	public ExpenseCategoryControllerAdvice(@Value("${api.docs.uri}") String API_DOCS_URI) {
		this.API_DOCS_URI = API_DOCS_URI;
	}

	@ExceptionHandler(ExpenseCategoryNameUnavailableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleExpenseCategoryNameUnavailableException(
			ExpenseCategoryNameUnavailableException ex, WebRequest req) {
		ErrorResponse errorResponse = ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage())
				.type(URI.create(API_DOCS_URI))
				.title("Expense category name unavailable")
				.instance(URI.create(req.getContextPath()))
				.property("timestamp", Instant.now())
				.build();

		return errorResponse;
	}

	@ExceptionHandler(ExpenseCategoryNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleExpenseCategoryNotFoundException(
			ExpenseCategoryNotFoundException ex, WebRequest req) {
		ErrorResponse errorResponse = ErrorResponse.builder(ex, HttpStatus.NOT_FOUND, ex.getMessage())
				.type(URI.create(API_DOCS_URI))
				.title("Expense category not found")
				.instance(URI.create(req.getContextPath()))
				.property("timestamp", Instant.now())
				.build();

		return errorResponse;
	}

}
