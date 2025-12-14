package com.jadebloom.goblin_api.shared.controller;

import java.net.URI;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalControllerAdvice {

	private final String API_DOCS_URI;

	public GlobalControllerAdvice(@Value("${api.docs.uri}") String API_DOCS_URI) {
		this.API_DOCS_URI = API_DOCS_URI;
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
		String errorMessage = ex.getConstraintViolations()
				.stream()
				.map(v -> v.getMessage() + ". ")
				.collect(Collectors.joining())
				.trim();

		ErrorResponse errorResponse = ErrorResponse
				.builder(ex, HttpStatus.BAD_REQUEST, errorMessage)
				.type(URI.create(API_DOCS_URI))
				.title("Violated Constraints")
				.build();

		return errorResponse;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleGenericException(Exception ex) {
		ErrorResponse errorResponse = ErrorResponse
				.builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong")
				.type(URI.create(API_DOCS_URI))
				.title("Something went wrong")
				.build();

		return errorResponse;
	}

}
