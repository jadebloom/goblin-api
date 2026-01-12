package com.jadebloom.goblin_api.security.enums;

public enum JwtTokenUseType {

    ACCESS("access"), REFRESH("refresh");

    private String name;

    private JwtTokenUseType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
