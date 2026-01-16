package com.jadebloom.goblin_api.security.controller;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jadebloom.goblin_api.security.dto.AuthenticationResponseDto;
import com.jadebloom.goblin_api.security.dto.JwtTokensDto;
import com.jadebloom.goblin_api.security.dto.LoginDto;
import com.jadebloom.goblin_api.security.dto.RegistrationDto;
import com.jadebloom.goblin_api.security.service.AuthenticationService;
import com.jadebloom.goblin_api.security.service.JwtService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	public AuthenticationController(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@PostMapping("/registration")
	public ResponseEntity<AuthenticationResponseDto> register(
			@Valid @RequestBody RegistrationDto dto) {
		JwtTokensDto jwt = authenticationService.register(dto);

		ResponseCookie cookie = ResponseCookie.from("refresh_token", jwt.getRefreshToken())
				.httpOnly(true)
				.secure(false)
				.path("/")
				.maxAge(JwtService.REFRESH_TOKEN_EXPIRATION)
				.sameSite("Lax")
				.build();

		AuthenticationResponseDto response = new AuthenticationResponseDto(jwt.getAccessToken());

		return ResponseEntity.ok()
				.header("Set-Cookie", cookie.toString())
				.body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<AuthenticationResponseDto> login(@Valid @RequestBody LoginDto dto) {
		JwtTokensDto jwt = authenticationService.login(dto);

		ResponseCookie cookie = ResponseCookie.from("refresh_token", jwt.getRefreshToken())
				.httpOnly(true)
				.secure(false)
				.path("/")
				.maxAge(JwtService.REFRESH_TOKEN_EXPIRATION)
				.sameSite("Lax")
				.build();

		AuthenticationResponseDto response = new AuthenticationResponseDto(jwt.getAccessToken());

		return ResponseEntity.ok()
				.header("Set-Cookie", cookie.toString())
				.body(response);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthenticationResponseDto> refresh(
			@CookieValue("refresh_token") String refreshToken) {
		JwtTokensDto jwt = authenticationService.refresh(refreshToken);

		ResponseCookie cookie = ResponseCookie.from("refresh_token", jwt.getRefreshToken())
				.httpOnly(true)
				.secure(false)
				.path("/")
				.maxAge(JwtService.REFRESH_TOKEN_EXPIRATION)
				.sameSite("Lax")
				.build();

		AuthenticationResponseDto response = new AuthenticationResponseDto(jwt.getAccessToken());

		return ResponseEntity.ok()
				.header("Set-Cookie", cookie.toString())
				.body(response);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout() {
		ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
				.httpOnly(true)
				.secure(false)
				.path("/")
				.maxAge(0)
				.sameSite("Lax")
				.build();

		return ResponseEntity.noContent()
				.header("Set-Cookie", cookie.toString())
				.build();
	}


}
