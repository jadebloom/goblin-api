package com.jadebloom.goblin_api.currency.dto;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jadebloom.goblin_api.currency.validation.ValidCurrencyAlphabeticalCode;
import com.jadebloom.goblin_api.currency.validation.ValidCurrencyName;

import jakarta.validation.constraints.NotNull;

public class CurrencyDto {

    @NotNull(message = "The currency's ID must not be null")
    private Long id;

    @ValidCurrencyName
    private String name;

    @ValidCurrencyAlphabeticalCode
    @JsonProperty("alphabetical_code")
    private String alphabeticalCode;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @NotNull(message = "The currency's creator ID must not be null")
    @JsonProperty("creator_id")
    private String creatorId;

    public CurrencyDto() {
    }

    public CurrencyDto(Long id, String name, ZonedDateTime createdAt, String creatorId) {
        this.id = id;

        this.name = name;

        this.createdAt = createdAt;

        this.creatorId = creatorId;
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

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCreatorId() {
        return creatorId;
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

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @Override
    public String toString() {
        return "CurrencyDto(id=" + id +
                ", name=" + name +
                ", alphabeticalCode=" + alphabeticalCode +
                ", createdAt=" + createdAt +
                ", creatorId=" + creatorId + ")";
    }

}
