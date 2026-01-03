package com.jadebloom.goblin_api.security.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jadebloom.goblin_api.security.error.IncorrectPasswordException;
import com.jadebloom.goblin_api.security.error.UserEmailInUseException;
import com.jadebloom.goblin_api.security.error.UserNotFoundException;

@RestControllerAdvice
public class AuthenticationControllerAdvice {

    private final String API_DOCS_URI;

    public AuthenticationControllerAdvice(@Value("${api.docs.uri}") String API_DOCS_URI) {
        this.API_DOCS_URI = API_DOCS_URI;
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectPasswordException(IncorrectPasswordException ex) {
        ErrorResponse errorResponse = ErrorResponse
                .builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage())
                .type(URI.create(API_DOCS_URI))
                .title("Incorrect password")
                .build();

        return errorResponse;
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse
                .builder(ex, HttpStatus.NOT_FOUND, ex.getMessage())
                .type(URI.create(API_DOCS_URI))
                .title("User wasn't found")
                .build();

        return errorResponse;
    }

    @ExceptionHandler(UserEmailInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserEmailInUseException(UserEmailInUseException ex) {
        ErrorResponse errorResponse = ErrorResponse
                .builder(ex, HttpStatus.CONFLICT, ex.getMessage())
                .type(URI.create(API_DOCS_URI))
                .title("User email is in use")
                .build();

        return errorResponse;
    }

}
