package com.jadebloom.goblin_api.expense.dto;

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

    public ExpenseCategoryDto() {
    }

    public ExpenseCategoryDto(String name) {
        this.name = name;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        String f = "ExpenseCategoryDto(id=%d, name=%s, description=%s)";

        return String.format(f, id, name, description);
    }

}
