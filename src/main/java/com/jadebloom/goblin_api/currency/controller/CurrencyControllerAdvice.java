package com.jadebloom.goblin_api.currency.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jadebloom.goblin_api.currency.error.CurrencyInUseException;
import com.jadebloom.goblin_api.currency.error.CurrencyNameUnavailableException;
import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;

@ControllerAdvice
public class CurrencyControllerAdvice {

    private final String API_DOCS_URI;

    public CurrencyControllerAdvice(@Value("${api.docs.uri}") String API_DOCS_URI) {
        this.API_DOCS_URI = API_DOCS_URI;
    }

    @ExceptionHandler(CurrencyNameUnavailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCurrencyNameUnavailableException(CurrencyNameUnavailableException ex) {
        ErrorResponse errorResponse = ErrorResponse
                .builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage())
                .type(URI.create(API_DOCS_URI))
                .title("Currency name unavailable")
                .build();

        return errorResponse;
    }

    @ExceptionHandler(CurrencyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCurrencyNotFoundException(CurrencyNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse
                .builder(ex, HttpStatus.NOT_FOUND, ex.getMessage())
                .type(URI.create(API_DOCS_URI))
                .title("Currency not found")
                .build();

        return errorResponse;
    }

    @ExceptionHandler(CurrencyInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleCurrencyInUseException(CurrencyInUseException ex) {
        ErrorResponse errorResponse = ErrorResponse
                .builder(ex, HttpStatus.CONFLICT, ex.getMessage())
                .type(URI.create(API_DOCS_URI))
                .title("Currency is in use")
                .build();

        return errorResponse;
    }

}
