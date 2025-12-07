package com.jadebloom.goblin_api.currency.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CurrencyDto {

    private Long id;

    @NotBlank(message = "The currency's name must not be null or empty")
    @Size(min = 1, max = 64, message = "The currency's name must be 1 - 64 characters long")
    private String name;

    @Pattern(regexp = "[A-Z][A-Z][A-Z]", message = "The currency's alphabetical code must conform to ISO 4217")
    @JsonProperty("alphabetical_code")
    private String alphabeticalCode;

    public CurrencyDto() {
    }

    public CurrencyDto(String name) {
        this.name = name;
    }

    public CurrencyDto(String name, String alphabeticalCode) {
        this.name = name;

        this.alphabeticalCode = alphabeticalCode;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAlphabeticalCode() {
        return alphabeticalCode;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAlphabeticalCode(String alphabeticalCode) {
        this.alphabeticalCode = alphabeticalCode;
    }

    @Override
    public String toString() {
        String f = "CurrencyDto(id=%d, name=%s, alphabeticalCode=%s)";

        return String.format(f, id, name, alphabeticalCode);
    }

}
