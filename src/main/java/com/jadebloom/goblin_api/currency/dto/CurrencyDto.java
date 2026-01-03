package com.jadebloom.goblin_api.currency.dto;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jadebloom.goblin_api.currency.validation.ValidCurrencyAlphabeticalCode;
import com.jadebloom.goblin_api.currency.validation.ValidCurrencyName;

import jakarta.validation.constraints.NotNull;

public class CurrencyDto {

	@NotNull(message = "The currency's ID must not be null")
	private Long id;

	@ValidCurrencyName
	private String name;

	@ValidCurrencyAlphabeticalCode
	@JsonProperty("alphabetical_code")
	private String alphabeticalCode;

	@JsonProperty("created_at")
	private ZonedDateTime createdAt;

	@NotNull(message = "The currency's creator ID must not be null")
	@JsonProperty("creator_id")
	private Long creatorId;

	public CurrencyDto() {
	}

	public CurrencyDto(Long id, String name, ZonedDateTime createdAt, Long creatorId) {
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

	public String getAlphabeticalCode() {
		return alphabeticalCode;
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

	public void setAlphabeticalCode(String alphabeticalCode) {
		this.alphabeticalCode = alphabeticalCode;
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

		CurrencyDto d = (CurrencyDto) o;

		if (id != d.getId() || name != d.getName() || alphabeticalCode != d.getAlphabeticalCode()) {
			return false;
		}

		return createdAt == d.getCreatedAt() && creatorId == d.getCreatorId();
	}

	@Override
	public String toString() {
		return "CurrencyDto(id=" + id +
				", name=" + name +
				", alphabeticalCode=" + alphabeticalCode +
				", createdAt=" + createdAt +
				", creatorId=" + creatorId + ")";
	}

}
