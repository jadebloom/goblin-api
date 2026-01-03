package com.jadebloom.goblin_api.security.util;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.jadebloom.goblin_api.security.config.CustomUserDetails;

public class SecurityContextUtils {

	public static Optional<Authentication> getAuthentication() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		return Optional.ofNullable(auth);
	}

	public static Optional<CustomUserDetails> getUserDetails() {
		Authentication auth = getAuthentication().orElse(null);

		if (auth == null) {
			return Optional.empty();
		}

		Object principal = auth.getPrincipal();

		if (principal == null || !(principal instanceof CustomUserDetails)) {
			return Optional.empty();
		}

		return Optional.of((CustomUserDetails) principal);
	}

	public static Optional<Long> getAuthenticatedUserId() {
		CustomUserDetails userDetails = getUserDetails().orElse(null);

		if (userDetails == null) {
			return Optional.empty();
		}

		return Optional.ofNullable(userDetails.getId());
	}

	public static Optional<String> getAuthenticatedUserEmail() {
		CustomUserDetails userDetails = getUserDetails().orElse(null);

		if (userDetails == null) {
			return Optional.empty();
		}

		return Optional.ofNullable(userDetails.getUsername());
	}

}
