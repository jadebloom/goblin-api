package com.jadebloom.goblin_api.expense.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseAmount;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseDescription;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseLabel;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseLabelsList;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseName;

import jakarta.validation.constraints.NotNull;

public class UpdateExpenseDto {

	@ValidExpenseName
	private String name;

	@ValidExpenseDescription
	private String description;

	@ValidExpenseAmount
	private Long amount;

	@ValidExpenseLabelsList
	private List<@ValidExpenseLabel String> labels;

	@NotNull(message = "The expense's category ID must not be null")
	@JsonProperty("expense_category_id")
	private Long expenseCategoryId;

	@NotNull(message = "The expense's currency ID must not be null")
	@JsonProperty("currency_id")
	private Long currencyId;

	public UpdateExpenseDto() {}

	public UpdateExpenseDto(String name, Long amount, Long expenseCategoryId, Long currencyId) {
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

	public Long getAmount() {
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

	public void setAmount(Long amount) {
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
		return "UpdateExpenseDto(name" + name +
				", description=" + description +
				", amount=" + amount +
				", labels=" + labels +
				", expenseCategoryId=" + expenseCategoryId +
				", currencyId=" + currencyId + ")";
	}

}
