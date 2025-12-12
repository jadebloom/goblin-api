package com.jadebloom.goblin_api.expense.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jadebloom.goblin_api.expense.validation.CheckLabel;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateExpenseDto {

    @NotBlank(message = "The expense's name must not be null or empty")
    @Size(min = 1, max = 64, message = "The expense's name must be 1 - 64 characters long")
    private String name;

    @Size(min = 1, max = 256, message = "The expense's name must be 1 - 256 characters long")
    private String description;

    @Min(value = 1, message = "The expense's amount must be at least 1")
    @Max(value = Integer.MAX_VALUE, message = "The expense's amount must be at most 2147483647")
    private Integer amount;

    @Size(max = 16, message = "The maximum amount of labels is 16")
    private List<@CheckLabel String> labels;

    @NotNull
    @JsonProperty("expense_category_id")
    private Long expenseCategoryId;

    @NotNull
    @JsonProperty("currency_id")
    private Long currencyId;

    public CreateExpenseDto() {
    }

    public CreateExpenseDto(String name, Integer amount, Long expenseCategoryId, Long currencyId) {
        this.name = name;

        this.amount = amount;

        this.expenseCategoryId = expenseCategoryId;

        this.currencyId = currencyId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getAmount() {
        return amount;
    }

    public List<String> getLabels() {
        return labels;
    }

    public Long getExpenseCategoryId() {
        return expenseCategoryId;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public void setExpenseCategoryId(Long expenseCategoryId) {
        this.expenseCategoryId = expenseCategoryId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    @Override
    public String toString() {
        String f = "CreateExpenseDto(name=" + name +
                ", description=" + description +
                ", amount=" + amount +
                ", labels=" + labels +
                ", expenseCategoryId=" + expenseCategoryId +
                ", currencyId=" + currencyId + ")";

        return f;
    }

}
