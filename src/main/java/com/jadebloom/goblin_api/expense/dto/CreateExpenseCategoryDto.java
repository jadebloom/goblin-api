package com.jadebloom.goblin_api.expense.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateExpenseCategoryDto {

    @NotBlank(message = "The expense category's name must not be null or empty")
    @Size(min = 1, max = 64, message = "The expense category's name must be 1 - 64 characters long")
    private String name;

    @Size(min = 1, max = 256, message = "The expense category's name must be 1 - 256 characters long")
    private String description;

    public CreateExpenseCategoryDto() {
    }

    public CreateExpenseCategoryDto(String name) {
        this.name = name;
    }

    public CreateExpenseCategoryDto(String name, String description) {
        this.name = name;

        this.description = description;
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

}
