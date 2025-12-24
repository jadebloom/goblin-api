package com.jadebloom.goblin_api.security.dto;

import com.jadebloom.goblin_api.security.validation.ValidUserPassword;
import com.jadebloom.goblin_api.security.validation.ValidUserEmail;

public class LoginDto {

    @ValidUserEmail
    private String email;

    @ValidUserPassword
    private String password;

    public LoginDto() {
    }

    public LoginDto(String email, String password) {
        this.email = email;

        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LoginDto loginDto = (LoginDto) o;

        return email == loginDto.getEmail() && password == loginDto.getPassword();
    }

    @Override
    public String toString() {
        return "LoginDto(email=" + email + ", password=" + password + ")";
    }

}
