package com.jadebloom.goblin_api.expense.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseAmount;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseDescription;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseLabel;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseLabelsList;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseName;
import com.jadebloom.goblin_api.shared.validation.ValidCurrencyCode;

import jakarta.validation.constraints.NotNull;

public class UpdateExpenseDto {

	@ValidExpenseName
	private String name;

	@ValidExpenseDescription
	private String description;

	@ValidExpenseAmount
	private Long amount;

	@ValidCurrencyCode
	@JsonProperty("currency_code")
	private String currencyCode;

	@ValidExpenseLabelsList
	private List<@ValidExpenseLabel String> labels;

	@NotNull(message = "The expense's category ID must not be null")
	@JsonProperty("expense_category_id")
	private Long expenseCategoryId;

	public UpdateExpenseDto() {
	}

	public UpdateExpenseDto(String name, Long amount, String currencyCode, Long expenseCategoryId) {
		this.name = name;

		this.amount = amount;

		this.currencyCode = currencyCode;

		this.expenseCategoryId = expenseCategoryId;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Long getAmount() {
		return amount;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public List<String> getLabels() {
		return labels;
	}

	public Long getExpenseCategoryId() {
		return expenseCategoryId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public void setExpenseCategoryId(Long expenseCategoryId) {
		this.expenseCategoryId = expenseCategoryId;
	}

	@Override
	public String toString() {
		return "UpdateExpenseDto(name" + name +
				", description=" + description +
				", amount=" + amount +
				", currencyCode=" + currencyCode +
				", labels=" + labels +
				", expenseCategoryId=" + expenseCategoryId + ")";
	}

}
