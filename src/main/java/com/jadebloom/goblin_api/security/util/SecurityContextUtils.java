package com.jadebloom.goblin_api.security.util;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextUtils {

    public static Optional<String> getAuthenticatedUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof String)) {
            return Optional.empty();
        }

        return Optional.of((String) auth.getPrincipal());
    }

}
