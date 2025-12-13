package com.jadebloom.goblin_api.currency.controller;

import java.net.URI;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import com.jadebloom.goblin_api.currency.error.CurrencyNameUnavailableException;
import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.currency.error.InvalidCurrencyException;
import com.jadebloom.goblin_api.shared.util.Links;

@ControllerAdvice
public class CurrencyControllerAdvice {

    @ExceptionHandler(InvalidCurrencyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidCurrencyException(InvalidCurrencyException ex, WebRequest req) {
        ErrorResponse errorResponse = ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage())
                .type(URI.create(Links.API_DOCS_URI))
                .title("Invalid currency")
                .instance(URI.create(req.getContextPath()))
                .property("timestamp", Instant.now())
                .build();

        return errorResponse;
    }

    @ExceptionHandler(CurrencyNameUnavailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCurrencyNameUnavailableException(
            CurrencyNameUnavailableException ex, WebRequest req) {
        ErrorResponse errorResponse = ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage())
                .type(URI.create(Links.API_DOCS_URI))
                .title("Currency name unavailable")
                .instance(URI.create(req.getContextPath()))
                .property("timestamp", Instant.now())
                .build();

        return errorResponse;
    }

    @ExceptionHandler(CurrencyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCurrencyNotFoundException(CurrencyNotFoundException ex, WebRequest req) {
        ErrorResponse errorResponse = ErrorResponse.builder(ex, HttpStatus.NOT_FOUND, ex.getMessage())
                .type(URI.create(Links.API_DOCS_URI))
                .title("Currency not found")
                .instance(URI.create(req.getContextPath()))
                .property("timestamp", Instant.now())
                .build();

        return errorResponse;
    }

}
