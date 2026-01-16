package com.jadebloom.goblin_api.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationResponseDto {

	@JsonProperty("access_token")
	private String accessToken;

	public AuthenticationResponseDto() {}

	public AuthenticationResponseDto(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	@Override
	public String toString() {
		return "AuthenticationResponseDto(accessToken=" + accessToken + ")";
	}

}
