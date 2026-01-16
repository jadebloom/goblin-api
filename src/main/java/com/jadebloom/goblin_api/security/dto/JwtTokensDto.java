package com.jadebloom.goblin_api.security.dto;

public class JwtTokensDto {

	private String accessToken;

	private String refreshToken;

	public JwtTokensDto() {
	}

	public JwtTokensDto(String accessToken, String refreshToken) {
		this.accessToken = accessToken;

		this.refreshToken = refreshToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@Override
	public String toString() {
		return "JwtResponseDto(accessToken=" + accessToken + ", refreshToken=" + refreshToken + ")";
	}

}
