package com.jadebloom.goblin_api.security.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.jadebloom.goblin_api.security.config.CustomUserDetails;
import com.jadebloom.goblin_api.security.dto.JwtTokensDto;
import com.jadebloom.goblin_api.security.dto.LoginDto;
import com.jadebloom.goblin_api.security.dto.RegistrationDto;
import com.jadebloom.goblin_api.security.entity.RoleEntity;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.error.IncorrectPasswordException;
import com.jadebloom.goblin_api.security.error.InvalidAuthenticationRequest;
import com.jadebloom.goblin_api.security.error.EmailUnavailableException;
import com.jadebloom.goblin_api.security.error.UserNotFoundException;
import com.jadebloom.goblin_api.security.repository.RoleRepository;
import com.jadebloom.goblin_api.security.repository.UserRepository;
import com.jadebloom.goblin_api.security.service.AuthenticationService;
import com.jadebloom.goblin_api.security.service.JwtService;
import com.jadebloom.goblin_api.security.util.SecurityContextUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;
import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	private final JwtService jwtService;

	private final PasswordEncoder passwordEncoder;

	private final AuthenticationManager authenticationManager;

	private final UserRepository userRepository;

	private final RoleRepository roleRepository;

	public AuthenticationServiceImpl(
			JwtService jwtService,
			PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager,
			UserRepository userRepository,
			RoleRepository roleRepository) {
		this.jwtService = jwtService;

		this.passwordEncoder = passwordEncoder;

		this.authenticationManager = authenticationManager;

		this.userRepository = userRepository;

		this.roleRepository = roleRepository;
	}

	@Override
	public JwtTokensDto register(RegistrationDto dto)
			throws InvalidAuthenticationRequest, EmailUnavailableException {
		if (!GenericValidator.isValid(dto)) {
			String message = GenericValidator.getValidationErrorMessage(dto);

			throw new InvalidAuthenticationRequest(message);
		}

		String email = dto.getEmail(), password = dto.getPassword();

		if (userRepository.existsByEmail(email)) {
			String f = "Email '%s' is already in use";

			throw new EmailUnavailableException(String.format(f, email));
		}

		// Roles and permissions are loaded manually and always persist.
		// So in case such role is not found, then the error must propagate as HTTP 500.
		RoleEntity userRole = roleRepository.findByName("ROLE_USER")
				.orElseThrow(() -> new RuntimeException("Failed to load user roles"));

		UserEntity toRegister = new UserEntity(
				email,
				passwordEncoder.encode(password),
				Set.of(userRole));
		UserEntity registered = userRepository.save(toRegister);

		Set<GrantedAuthority> userGrantedAuthorities = new HashSet<>();
		Set<String> userRoleNames = new HashSet<>();
		for (RoleEntity role : registered.getRoles()) {
			userGrantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));

			userRoleNames.add(role.getName());
		}

		Authentication request = new UsernamePasswordAuthenticationToken(
				email,
				password,
				userGrantedAuthorities);
		authenticationManager.authenticate(request);

		String accessToken = jwtService.generateAccessToken(
				registered.getId(), email, userRoleNames);
		String refreshToken = jwtService.generateRefreshToken(registered.getId());

		return new JwtTokensDto(accessToken, refreshToken);
	}

	@Override
	public JwtTokensDto login(LoginDto dto)
			throws InvalidAuthenticationRequest,
			UserNotFoundException,
			IncorrectPasswordException {
		if (!GenericValidator.isValid(dto)) {
			String message = GenericValidator.getValidationErrorMessage(dto);

			throw new InvalidAuthenticationRequest(message);
		}

		String email = dto.getEmail(), password = dto.getPassword();

		UserEntity user = userRepository.findByEmail(email)
				.orElseThrow(() -> {
					String f = "User with the email '%s' wasn't found";

					throw new UserNotFoundException(String.format(f, email));
				});

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new IncorrectPasswordException("Provided password is incorrect");
		}

		Set<GrantedAuthority> userGrantedAuthorities = new HashSet<>();
		Set<String> userRoleNames = new HashSet<>();
		for (RoleEntity role : user.getRoles()) {
			userGrantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));

			userRoleNames.add(role.getName());
		}

		Authentication request = new UsernamePasswordAuthenticationToken(
				email,
				password,
				userGrantedAuthorities);
		authenticationManager.authenticate(request);

		String accessToken = jwtService.generateAccessToken(user.getId(), email, userRoleNames);
		String refreshToken = jwtService.generateRefreshToken(user.getId());

		return new JwtTokensDto(accessToken, refreshToken);
	}

	@Override
	public JwtTokensDto refresh(String refreshToken) throws JWTVerificationException {
		Long userId = jwtService.validateRefreshTokenAndRetrieveUserId(refreshToken);

		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> {
					String f = "User with the ID '%s' wasn't found";

					throw new UserNotFoundException(String.format(f, userId));
				});

		Set<GrantedAuthority> authorities = new HashSet<>();
		Set<String> roleNames = new HashSet<>();
		for (RoleEntity role : user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
			roleNames.add(role.getName());
		}

		Authentication auth = new UsernamePasswordAuthenticationToken(
				new CustomUserDetails(user.getId(), user.getEmail(), null, authorities),
				null,
				authorities);
		SecurityContextHolder.getContext().setAuthentication(auth);

		String accessToken =
				jwtService.generateAccessToken(user.getId(), user.getEmail(), roleNames);
		String newRefreshToken = jwtService.generateRefreshToken(user.getId());

		return new JwtTokensDto(accessToken, newRefreshToken);
	}

	@Override
	public void logout() throws InvalidAuthenticationRequest {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		userRepository.findById(userId)
				.orElseThrow(() -> {
					String f = "User with the ID '%s' wasn't found";

					throw new UserNotFoundException(String.format(f, userId));
				});
	}

}
