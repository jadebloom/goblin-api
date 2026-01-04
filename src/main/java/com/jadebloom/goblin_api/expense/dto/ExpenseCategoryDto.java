package com.jadebloom.goblin_api.expense.dto;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseCategoryDescription;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseCategoryName;
import com.jadebloom.goblin_api.shared.validation.ValidHexColorCode;

import jakarta.validation.constraints.NotNull;

public class ExpenseCategoryDto {

	@NotNull(message = "The expense category's ID must not be null")
	private Long id;

	@ValidExpenseCategoryName
	private String name;

	@ValidExpenseCategoryDescription
	private String description;

	@ValidHexColorCode
	@JsonProperty("hex_color_code")
	private String hexColorCode;

	@JsonProperty("created_at")
	private ZonedDateTime createdAt;

	@NotNull(message = "The expense category's creator ID must not be null")
	@JsonProperty("creator_id")
	private Long creatorId;

	public ExpenseCategoryDto() {
	}

	public ExpenseCategoryDto(Long id, String name, ZonedDateTime createdAt, Long creatorId) {
		this.id = id;

		this.name = name;

		this.createdAt = createdAt;

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

	public String getHexColorCode() {
		return hexColorCode;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
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

	public void setHexColorCode(String hexColorCode) {
		this.hexColorCode = hexColorCode;
	}

	public void setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ExpenseCategoryDto d = (ExpenseCategoryDto) o;

		if (id != d.getId() || !name.equals(d.getName())) {
			return false;
		}

		if (description != null && !description.equals(d.getDescription())) {
			return false;
		}

		if (hexColorCode != null && !hexColorCode.equals(d.getHexColorCode())) {
			return false;
		}

		return createdAt == d.getCreatedAt() && creatorId == d.getCreatorId();
	}

	@Override
	public String toString() {
		return "ExpenseCategoryDto(id=" + id +
				", name=" + name +
				", description=" + description +
				", hexColorCode=" + hexColorCode +
				", createdAt=" + createdAt +
				", creatorId=" + creatorId + ")";
	}

}
