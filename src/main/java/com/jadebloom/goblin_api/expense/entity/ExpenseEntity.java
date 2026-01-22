package com.jadebloom.goblin_api.expense.entity;

import java.time.ZonedDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.jadebloom.goblin_api.expense.validation.ValidExpenseAmount;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseDescription;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseLabel;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseLabelsList;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseName;
import com.jadebloom.goblin_api.expense_category.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.shared.validation.ValidCurrencyCode;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "expense")
public class ExpenseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable = false)
	private Long id;

	@Column(nullable = false, length = 64)
	@ValidExpenseName
	private String name;

	@Column(nullable = false)
	@ValidExpenseAmount
	private Long amount;

	@Column(length = 3)
	@ValidCurrencyCode
	private String currencyCode;

	@Column(length = 256)
	@ValidExpenseDescription
	private String description;

	@ElementCollection
	@CollectionTable(name = "expense_labels", joinColumns = @JoinColumn(name = "expense_id"))
	@Column(name = "label", length = 32)
	@ValidExpenseLabelsList
	private List<@ValidExpenseLabel String> labels;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private ZonedDateTime createdAt;

	@ManyToOne(optional = false)
	@JoinColumn(name = "expense_category_id", referencedColumnName = "id", nullable = false)
	@NotNull(message = "The expense's category must not be null")
	private ExpenseCategoryEntity expenseCategory;

	@ManyToOne(optional = false)
	@JoinColumn(name = "creator_id", referencedColumnName = "id", nullable = false, updatable = false)
	@NotNull(message = "The expense's creator must not be null")
	private UserEntity creator;

	public ExpenseEntity() {
	}

	public ExpenseEntity(
			String name,
			Long amount,
			String currencyCode,
			ExpenseCategoryEntity expenseCategory,
			UserEntity creator) {
		this.name = name;

		this.amount = amount;

		this.expenseCategory = expenseCategory;

		this.currencyCode = currencyCode;

		this.creator = creator;
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

	public String getCurrencyCode() {
		return currencyCode;
	}

	public List<String> getLabels() {
		return labels;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public ExpenseCategoryEntity getExpenseCategory() {
		return expenseCategory;
	}

	public UserEntity getCreator() {
		return creator;
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

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public void setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setExpenseCategory(ExpenseCategoryEntity expenseCategory) {
		this.expenseCategory = expenseCategory;
	}

	public void setCreator(UserEntity creator) {
		this.creator = creator;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ExpenseEntity e = (ExpenseEntity) o;

		if (id != e.getId() || !name.equals(e.getName())) {
			return false;
		}

		if (description != null && !description.equals(e.getDescription())) {
			return false;
		}

		return amount == e.amount && currencyCode.equals(e.getCurrencyCode()) && createdAt == e.createdAt;
	}

	@Override
	public String toString() {
		return "ExpenseEntity(id=" + id +
				", name=" + name +
				", description=" + description +
				", amount=" + amount +
				", currencyCode=" + currencyCode +
				", createdAt=" + createdAt + ")";
	}

}
