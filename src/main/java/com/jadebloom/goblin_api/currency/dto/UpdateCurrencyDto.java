package com.jadebloom.goblin_api.currency.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jadebloom.goblin_api.currency.validation.ValidCurrencyAlphabeticalCode;
import com.jadebloom.goblin_api.currency.validation.ValidCurrencyName;

import jakarta.validation.constraints.NotNull;

public class UpdateCurrencyDto {

    @NotNull
    private Long id;

    @ValidCurrencyName
    private String name;

    @ValidCurrencyAlphabeticalCode
    @JsonProperty("alphabetical_code")
    private String alphabeticalCode;

    public UpdateCurrencyDto() {
    }

    public UpdateCurrencyDto(Long id, String name) {
        this.id = id;

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
        return "UpdateCurrencyDto(" +
                "id=" + id +
                ", name=" + name +
                ", alphabeticalCode=" + alphabeticalCode + ")";
    }

}
