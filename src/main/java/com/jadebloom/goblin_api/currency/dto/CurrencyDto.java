package com.jadebloom.goblin_api.currency.dto;

public class CurrencyDto {

    private Long id;

    private String name;

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
        String f = "CurrencyDto(id=%d, name=%s, alphabeticalCode=%s";

        return String.format(f, id, name, alphabeticalCode);
    }

}
