package com.jadebloom.goblin_api.expense.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jadebloom.goblin_api.shared.validation.ValidIdList;

public class DeleteExpensesDto {

	@ValidIdList
	@JsonProperty("expense_ids")
	private List<Long> expenseIds;

	public DeleteExpensesDto() {}

	public DeleteExpensesDto(List<Long> expenseIds) {
		this.expenseIds = expenseIds;
	}

	public List<Long> getExpenseIds() {
		return expenseIds;
	}

	public void setExpenseIds(List<Long> expenseIds) {
		this.expenseIds = expenseIds;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DeleteExpensesDto d = (DeleteExpensesDto) o;

		if (expenseIds.size() != d.getExpenseIds().size()) {
			return false;
		}

		Map<Long, Integer> map = new HashMap<>();

		for (Long l : expenseIds) {
			map.put(l, map.getOrDefault(l, 0) + 1);
		}

		for (Long l : d.getExpenseIds()) {
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
		return "DeleteExpensesDto(expenseIds=" + expenseIds + ")";
	}

}
