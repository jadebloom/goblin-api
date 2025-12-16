package com.jadebloom.goblin_api.currency.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jadebloom.goblin_api.currency.validation.ValidCurrencyAlphabeticalCode;
import com.jadebloom.goblin_api.currency.validation.ValidCurrencyName;

public class CreateCurrencyDto {

    @ValidCurrencyName
    private String name;

    @ValidCurrencyAlphabeticalCode
    @JsonProperty("alphabetical_code")
    private String alphabeticalCode;

    public CreateCurrencyDto() {
    }

    public CreateCurrencyDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getAlphabeticalCode() {
        return alphabeticalCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAlphabeticalCode(String alphabeticalCode) {
        this.alphabeticalCode = alphabeticalCode;
    }

    @Override
    public String toString() {
        return "CreateCurrencyDto(" +
                "name=" + name +
                ", alphabeticalCode=" + alphabeticalCode + ")";
    }

}
