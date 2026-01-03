package com.jadebloom.goblin_api.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JwtResponseDto {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    public JwtResponseDto() {
    }

    public JwtResponseDto(String accessToken, String refreshToken) {
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
        return "JwtResponseDto(accessToken=" + accessToken + ", refreshToken" + refreshToken + ")";
    }

}
