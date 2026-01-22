package com.jadebloom.goblin_api.expense_category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jadebloom.goblin_api.expense_category.validation.ValidExpenseCategoryDescription;
import com.jadebloom.goblin_api.expense_category.validation.ValidExpenseCategoryName;
import com.jadebloom.goblin_api.shared.validation.ValidHexColorCode;

public class CreateExpenseCategoryDto {

	@ValidExpenseCategoryName
	private String name;

	@ValidExpenseCategoryDescription
	private String description;

	@ValidHexColorCode
	@JsonProperty("hex_color_code")
	private String hexColorCode;

	public CreateExpenseCategoryDto() {}

	public CreateExpenseCategoryDto(String name) {
		this.name = name;
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
		return "CreateExpenseCategoryDto(name=" + name +
				", description=" + description +
				", hexColorCode=" + hexColorCode + ")";
	}

}
