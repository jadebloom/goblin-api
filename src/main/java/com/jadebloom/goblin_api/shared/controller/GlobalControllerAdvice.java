package com.jadebloom.goblin_api.shared.controller;

import java.net.URI;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.jadebloom.goblin_api.shared.error.ForbiddenException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

	private final String API_DOCS_URI;

	public GlobalControllerAdvice(@Value("${api.docs.uri}") String API_DOCS_URI) {
		this.API_DOCS_URI = API_DOCS_URI;
	}

	@Override
	protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		String errorMessage = "";

		for (ObjectError oe : ex.getAllErrors()) {
			errorMessage += oe.getDefaultMessage() + ". ";
		}

		ErrorResponse errorResponse = ErrorResponse
				.builder(ex, HttpStatus.BAD_REQUEST, errorMessage.trim())
				.type(URI.create(API_DOCS_URI))
				.title("Invalid Argument")
				.build();

		return new ResponseEntity<>(errorResponse.getBody(), HttpStatus.BAD_REQUEST);
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

	@ExceptionHandler(ForbiddenException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorResponse handleForbiddenException(ForbiddenException ex) {
		ErrorResponse errorResponse = ErrorResponse
				.builder(ex, HttpStatus.FORBIDDEN, "Insufficient permissions")
				.type(URI.create(API_DOCS_URI))
				.title("Forbidden")
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
