package com.jadebloom.goblin_api.account.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.jadebloom.goblin_api.account.errors.InvalidPasswordException;

@RestControllerAdvice
public class AccountControllerAdvice {

	private final String API_DOCS_URI;

	public AccountControllerAdvice(@Value("${api.docs.uri}") String API_DOCS_URI) {
		this.API_DOCS_URI = API_DOCS_URI;
	}

	@ExceptionHandler(InvalidPasswordException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleInvalidPasswordException(InvalidPasswordException ex) {
		ErrorResponse errorResponse =
				ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage())
						.type(URI.create(API_DOCS_URI)).title("Invalid password").build();

		return errorResponse;
	}

}
