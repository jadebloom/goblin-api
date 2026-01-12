package com.jadebloom.goblin_api.security.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.jadebloom.goblin_api.security.dto.JwtResponseDto;
import com.jadebloom.goblin_api.security.dto.LoginDto;
import com.jadebloom.goblin_api.security.dto.RegistrationDto;
import com.jadebloom.goblin_api.security.error.IncorrectPasswordException;
import com.jadebloom.goblin_api.security.error.InvalidAuthenticationRequest;
import com.jadebloom.goblin_api.security.error.EmailUnavailableException;
import com.jadebloom.goblin_api.security.error.UserNotFoundException;

public interface AuthenticationService {

	JwtResponseDto register(RegistrationDto dto)
			throws InvalidAuthenticationRequest, EmailUnavailableException;

	JwtResponseDto login(LoginDto dto)
			throws InvalidAuthenticationRequest,
			UserNotFoundException,
			IncorrectPasswordException;

	JwtResponseDto refresh(String refreshToken) throws JWTVerificationException;

	// logout

}
