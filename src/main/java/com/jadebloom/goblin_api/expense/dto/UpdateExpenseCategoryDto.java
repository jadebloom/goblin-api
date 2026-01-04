package com.jadebloom.goblin_api.expense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseCategoryDescription;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseCategoryName;
import com.jadebloom.goblin_api.shared.validation.ValidHexColorCode;

import jakarta.validation.constraints.NotNull;

public class UpdateExpenseCategoryDto {

	@NotNull(message = "The expense category's ID must not be null")
	private Long id;

	@ValidExpenseCategoryName
	private String name;

	@ValidExpenseCategoryDescription
	private String description;

	@ValidHexColorCode
	@JsonProperty("hex_color_code")
	private String hexColorCode;

	public UpdateExpenseCategoryDto() {
	}

	public UpdateExpenseCategoryDto(Long id, String name) {
		this.id = id;

		this.name = name;
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

	@Override
	public String toString() {
		return "UpdateExpenseCategoryDto(id=" + id +
				", name=" + name +
				", description=" + description +
				", hexColorCode=" + hexColorCode + ")";
	}

}
