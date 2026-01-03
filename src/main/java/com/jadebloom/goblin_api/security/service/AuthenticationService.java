package com.jadebloom.goblin_api.security.service;

import com.jadebloom.goblin_api.security.dto.JwtResponseDto;
import com.jadebloom.goblin_api.security.dto.LoginDto;
import com.jadebloom.goblin_api.security.dto.RegistrationDto;
import com.jadebloom.goblin_api.security.error.IncorrectPasswordException;
import com.jadebloom.goblin_api.security.error.UserEmailInUseException;
import com.jadebloom.goblin_api.security.error.UserNotFoundException;

public interface AuthenticationService {

    JwtResponseDto register(RegistrationDto dto) throws UserEmailInUseException;

    JwtResponseDto login(LoginDto dto) throws UserNotFoundException, IncorrectPasswordException;

    // logout

    // account deletion

}
