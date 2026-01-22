package com.jadebloom.goblin_api.expense_category.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jadebloom.goblin_api.shared.validation.ValidIdList;

public class DeleteExpenseCategoriesDto {

	@ValidIdList
	@JsonProperty("expense_category_ids")
	private List<Long> expenseCategoryIds;

	public DeleteExpenseCategoriesDto() {}

	public DeleteExpenseCategoriesDto(List<Long> expenseCategoryIds) {
		this.expenseCategoryIds = expenseCategoryIds;
	}

	public List<Long> getExpenseCategoryIds() {
		return expenseCategoryIds;
	}

	public void setExpenseCategoryIds(List<Long> expenseCategoryIds) {
		this.expenseCategoryIds = expenseCategoryIds;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DeleteExpenseCategoriesDto d = (DeleteExpenseCategoriesDto) o;

		if (expenseCategoryIds.size() != d.getExpenseCategoryIds().size()) {
			return false;
		}

		Map<Long, Integer> map = new HashMap<>();

		for (Long l : expenseCategoryIds) {
			map.put(l, map.getOrDefault(l, 0) + 1);
		}

		for (Long l : d.getExpenseCategoryIds()) {
			map.put(l, map.getOrDefault(l, 0) - 1);
		}

		for (int v : map.values()) {
			if (v != 0) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		return "DeleteExpenseCategoriesDto(expenseCategoryIds=" + expenseCategoryIds + ")";
	}

}
