package com.jadebloom.goblin_api.expense_category.entity;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;
import com.jadebloom.goblin_api.expense_category.validation.ValidExpenseCategoryDescription;
import com.jadebloom.goblin_api.expense_category.validation.ValidExpenseCategoryName;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.shared.validation.ValidHexColorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "expense_category")
public class ExpenseCategoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable = false)
	private Long id;

	@Column(unique = true, nullable = false, length = 64)
	@ValidExpenseCategoryName
	private String name;

	@Column(length = 256)
	@ValidExpenseCategoryDescription
	private String description;

	@Column(length = 7)
	@ValidHexColorCode
	private String hexColorCode;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private ZonedDateTime createdAt;

	@ManyToOne(optional = false)
	@JoinColumn(name = "creator_id", referencedColumnName = "id", nullable = false,
			updatable = false)
	@NotNull(message = "The expense category's creator must not be null")
	private UserEntity creator;

	public ExpenseCategoryEntity() {}

	public ExpenseCategoryEntity(String name, UserEntity creator) {
		this.name = name;

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

	public String getHexColorCode() {
		return hexColorCode;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
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

	public void setHexColorCode(String hexColorCode) {
		this.hexColorCode = hexColorCode;
	}

	public void setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
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

		ExpenseCategoryEntity expenseCategory = (ExpenseCategoryEntity) o;

		if (id != expenseCategory.getId() || !name.equals(expenseCategory.getName())) {
			return false;
		}

		if (description != null && !description.equals(expenseCategory.getDescription())) {
			return false;
		}

		if (hexColorCode != null && !hexColorCode.equals(expenseCategory.getHexColorCode())) {
			return false;
		}

		return createdAt == expenseCategory.getCreatedAt();
	}

	@Override
	public String toString() {
		return "ExpenseCategoryEntity(id=" + id +
				", name=" + name +
				", description=" + description +
				", hexColorCode=" + hexColorCode +
				", createdAt=" + createdAt + ")";
	}

}
