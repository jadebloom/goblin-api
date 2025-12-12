package com.jadebloom.goblin_api.currency.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jadebloom.goblin_api.currency.validation.ValidCurrencyAlphabeticalCode;
import com.jadebloom.goblin_api.currency.validation.ValidCurrencyName;

import jakarta.validation.constraints.NotNull;

public class CurrencyDto {

    @NotNull
    private Long id;

    @ValidCurrencyName
    private String name;

    @ValidCurrencyAlphabeticalCode
    @JsonProperty("alphabetical_code")
    private String alphabeticalCode;

    public CurrencyDto() {
    }

    public CurrencyDto(String name) {
        this.name = name;
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
