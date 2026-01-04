package com.jadebloom.goblin_api.expense.dto;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseAmount;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseDescription;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseLabel;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseLabelsList;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseName;

import jakarta.validation.constraints.NotNull;

public class ExpenseDto {

	@NotNull(message = "The expense's ID must not be null")
	private Long id;

	@ValidExpenseName
	private String name;

	@ValidExpenseDescription
	private String description;

	@ValidExpenseAmount
	private Long amount;

	@ValidExpenseLabelsList
	private List<@ValidExpenseLabel String> labels;

	@JsonProperty("created_at")
	private ZonedDateTime createdAt;

	@NotNull(message = "The expense's creator ID is not null")
	@JsonProperty("expense_category_id")
	private Long expenseCategoryId;

	@NotNull(message = "The expense's creator ID is not null")
	@JsonProperty("currency_id")
	private Long currencyId;

	@NotNull(message = "The expense's creator ID is not null")
	@JsonProperty("creator_id")
	private Long creatorId;

	public ExpenseDto() {
	}

	public ExpenseDto(
			Long id,
			String name,
			Long amount,
			ZonedDateTime createdAt,
			Long expenseCategoryId,
			Long currencyId,
			Long creatorId) {
		this.id = id;

		this.name = name;

		this.amount = amount;

		this.createdAt = createdAt;

		this.expenseCategoryId = expenseCategoryId;

		this.currencyId = currencyId;

		this.creatorId = creatorId;
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

	public Long getAmount() {
		return amount;
	}

	public List<String> getLabels() {
		return labels;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public Long getExpenseCategoryId() {
		return expenseCategoryId;
	}

	public Long getCurrencyId() {
		return currencyId;
	}

	public Long getCreatorId() {
		return creatorId;
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

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public void setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setExpenseCategoryId(Long expenseCategoryId) {
		this.expenseCategoryId = expenseCategoryId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (getClass() != o.getClass()) {
			return false;
		}

		ExpenseDto t = (ExpenseDto) o;

		if (id != t.getId() || !name.equals(t.getName()) || amount != t.getAmount()) {
			return false;
		}

		if (description != null && !description.equals(t.getDescription())) {
			return false;
		}

		if (labels != null && !labels.equals(t.getLabels())) {
			return false;
		}

		if (expenseCategoryId != t.getExpenseCategoryId() || currencyId != t.getCurrencyId()) {
			return false;
		}

		return creatorId == t.getCreatorId();
	}

	@Override
	public String toString() {
		String f = "ExpenseDto(id=" + id +
				", name=" + name +
				", description=" + description +
				", amount=" + amount +
				", labels=" + labels +
				", createdAt=" + createdAt +
				", expenseCategoryId=" + expenseCategoryId +
				", currencyId=" + currencyId +
				", creatorId=" + creatorId + ")";

		return f;
	}

}
