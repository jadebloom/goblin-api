package com.jadebloom.goblin_api.security.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jadebloom.goblin_api.security.config.CustomUserDetails;

@Service
public class JwtService {

	private String jwtSecret;

	private static final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000; // 15 min

	private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7 days

	public JwtService(@Value("${JWT_SECRET}") String jwtSecret) {
		this.jwtSecret = jwtSecret;
	}

	public String generateAccessToken(Long userId, String userEmail, Set<String> roles) {
		return JWT.create()
				.withSubject("" + userId)
				.withClaim("email", userEmail)
				.withClaim("roles", new ArrayList<>(roles))
				.withIssuedAt(new Date())
				.withIssuer("Goblin API")
				.withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
				.sign(Algorithm.HMAC256(jwtSecret));
	}

	public String generateRefreshToken(Long userId, String userEmail, Set<String> roles) {
		return JWT.create()
				.withSubject("" + userId)
				.withClaim("email", userEmail)
				.withClaim("roles", new ArrayList<>(roles))
				.withIssuedAt(new Date())
				.withIssuer("Goblin API")
				.withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
				.sign(Algorithm.HMAC256(jwtSecret));
	}

	public Authentication validateTokenAndRetrieveEmail(String token) {
		DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(jwtSecret))
				.build()
				.verify(token);

		Long userId = decodedJWT.getClaim("sub").asLong();
		String userEmail = decodedJWT.getClaim("email").asString();

		List<String> roleNames = decodedJWT.getClaim("roles").asList(String.class);
		Set<String> uniqueRoleNames = new HashSet<>(roleNames);

		Set<GrantedAuthority> roles = uniqueRoleNames.stream()
				.map(n -> new SimpleGrantedAuthority(n))
				.collect(Collectors.toSet());

		CustomUserDetails userDetails = new CustomUserDetails(userId, userEmail, "", roles);

		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
				userDetails,
				"",
				roles);

		return auth;
	}

}
