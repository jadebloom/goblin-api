package com.jadebloom.goblin_api.security.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.jadebloom.goblin_api.security.dto.JwtTokensDto;
import com.jadebloom.goblin_api.security.dto.LoginDto;
import com.jadebloom.goblin_api.security.dto.RegistrationDto;
import com.jadebloom.goblin_api.security.error.IncorrectPasswordException;
import com.jadebloom.goblin_api.security.error.InvalidAuthenticationRequest;
import com.jadebloom.goblin_api.security.error.EmailUnavailableException;
import com.jadebloom.goblin_api.security.error.UserNotFoundException;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

public interface AuthenticationService {

	JwtTokensDto register(RegistrationDto dto)
			throws InvalidAuthenticationRequest, EmailUnavailableException;

	JwtTokensDto login(LoginDto dto)
			throws InvalidAuthenticationRequest,
			UserNotFoundException,
			IncorrectPasswordException;

	JwtTokensDto refresh(String refreshToken) throws JWTVerificationException;

	void logout() throws ForbiddenException, UserNotFoundException, InvalidAuthenticationRequest;

}
