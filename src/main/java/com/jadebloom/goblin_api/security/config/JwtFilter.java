package com.jadebloom.goblin_api.security.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.jadebloom.goblin_api.security.service.JwtService;
import com.jadebloom.goblin_api.shared.service.HttpResponseService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	private final HttpResponseService httpResponseService;

	public JwtFilter(JwtService jwtService, HttpResponseService httpResponseService) {
		this.jwtService = jwtService;

		this.httpResponseService = httpResponseService;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletRequest httpReq = (HttpServletRequest) request;

		String bearer = httpReq.getHeader("Authorization");
		if (bearer == null || !bearer.startsWith("Bearer ") || bearer.length() < 8) {
			filterChain.doFilter(request, response);

			return;
		}
		String token = bearer.substring(7);

		try {
			Authentication auth = jwtService.validateAccessTokenAndPrepareUserDetails(token);

			SecurityContextHolder.getContext().setAuthentication(auth);

			filterChain.doFilter(request, response);
		} catch (JWTVerificationException e) {
			httpResponseService.writeHttpErrorResponse(response, e);
		}
	}

}
