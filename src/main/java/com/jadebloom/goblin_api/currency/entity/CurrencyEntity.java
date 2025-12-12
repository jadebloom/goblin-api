package com.jadebloom.goblin_api.currency.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jadebloom.goblin_api.currency.validation.ValidCurrencyAlphabeticalCode;
import com.jadebloom.goblin_api.currency.validation.ValidCurrencyName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "currency")
public class CurrencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false, length = 64)
    @ValidCurrencyName
    private String name;

    @Column(name = "alphabetical_code", length = 3)
    @ValidCurrencyAlphabeticalCode
    @JsonProperty("alphabetical_code")
    private String alphabeticalCode;

    public CurrencyEntity() {
    }

    public CurrencyEntity(String name) {
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CurrencyEntity currencyEntity = (CurrencyEntity) o;

        if (id != currencyEntity.getId() || name != currencyEntity.getName()) {
            return false;
        }

        return alphabeticalCode == currencyEntity.getAlphabeticalCode();
    }

    @Override
    public String toString() {
        String f = "CurrencyEntity(id=%d, name=%s, alphabeticalCode=%s)";

        return String.format(f, id, name, alphabeticalCode);
    }

}
