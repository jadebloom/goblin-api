package com.jadebloom.goblin_api.shared.controller;

import java.net.URI;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import com.jadebloom.goblin_api.shared.util.Links;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex, WebRequest req) {
        ErrorResponse errorResponse = ErrorResponse
                .builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong")
                .type(URI.create(Links.API_DOCS_URI + "#exception-error"))
                .title("Something went wrong")
                .instance(URI.create(req.getContextPath()))
                .property("timestamp", Instant.now())
                .build();

        return errorResponse;
    }

}
