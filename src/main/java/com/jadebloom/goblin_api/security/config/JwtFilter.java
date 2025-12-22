package com.jadebloom.goblin_api.security.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jadebloom.goblin_api.security.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	public JwtFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletRequest httpReq = (HttpServletRequest) request;

		String bearer = httpReq.getHeader("Authorization");

		if (bearer == null || !bearer.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);

			return;
		}

		String token = bearer.substring(7);

		Authentication auth = jwtService.validateTokenAndRetrieveEmail(token);

		SecurityContextHolder.getContext().setAuthentication(auth);

		filterChain.doFilter(request, response);
	}

}
