package com.jadebloom.goblin_api.expense.dto;

import com.jadebloom.goblin_api.expense.validation.ValidExpenseCategoryDescription;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseCategoryName;

public class CreateExpenseCategoryDto {

    @ValidExpenseCategoryName
    private String name;

    @ValidExpenseCategoryDescription
    private String description;

    public CreateExpenseCategoryDto() {
    }

    public CreateExpenseCategoryDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        String f = "CreateExpenseCategoryDto(name=%s, description=%s)";

        return String.format(f, name, description);
    }

}
