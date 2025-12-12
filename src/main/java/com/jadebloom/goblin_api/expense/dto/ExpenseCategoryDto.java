package com.jadebloom.goblin_api.expense.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ExpenseCategoryDto {

    @NotNull
    private Long id;

    @NotBlank(message = "The expense category's name must not be null or empty")
    @Size(min = 1, max = 64, message = "The expense category's name must be 1 - 64 characters long")
    private String name;

    @Size(min = 1, max = 256, message = "The expense category's name must be 1 - 256 characters long")
    private String description;

    public ExpenseCategoryDto() {
    }

    public ExpenseCategoryDto(String name) {
        this.name = name;
    }

    public ExpenseCategoryDto(String name, String description) {
        this.name = name;

        this.description = description;
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
