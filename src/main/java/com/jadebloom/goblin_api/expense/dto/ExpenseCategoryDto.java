package com.jadebloom.goblin_api.expense.dto;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseCategoryDescription;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseCategoryName;

import jakarta.validation.constraints.NotNull;

public class ExpenseCategoryDto {

    @NotNull
    private Long id;

    @ValidExpenseCategoryName
    private String name;

    @ValidExpenseCategoryDescription
    private String description;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    public ExpenseCategoryDto() {
    }

    public ExpenseCategoryDto(Long id, String name, ZonedDateTime createdAt) {
        this.id = id;

        this.name = name;

        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ExpenseCategoryDto(" +
                "id=" + id +
                ", name=" + name +
                ", description=" + description +
                ", createdAt=" + createdAt + ")";
    }

}
