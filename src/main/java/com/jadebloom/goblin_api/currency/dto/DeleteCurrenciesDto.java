package com.jadebloom.goblin_api.currency.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jadebloom.goblin_api.shared.validation.ValidIdList;

public class DeleteCurrenciesDto {

	@ValidIdList
	@JsonProperty("currency_ids")
	private List<Long> currencyIds;

	public DeleteCurrenciesDto() {}

	public DeleteCurrenciesDto(List<Long> currencyIds) {
		this.currencyIds = currencyIds;
	}

	public List<Long> getCurrencyIds() {
		return currencyIds;
	}

	public void setCurrencyIds(List<Long> currencyIds) {
		this.currencyIds = currencyIds;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DeleteCurrenciesDto d = (DeleteCurrenciesDto) o;

		if (currencyIds.size() != d.getCurrencyIds().size()) {
			return false;
		}

		Map<Long, Integer> map = new HashMap<>();

		for (Long l : currencyIds) {
			map.put(l, map.getOrDefault(l, 0) + 1);
		}

		for (Long l : d.getCurrencyIds()) {
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
		return "DeleteCurrenciesDto(currencyIds=" + currencyIds + ")";
	}

}
