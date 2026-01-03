package com.jadebloom.goblin_api.security.util;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class SecurityContextUtils {

    public static Optional<String> getAuthenticatedUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            return Optional.empty();
        }

        if (auth.getPrincipal() instanceof String) {
            return Optional.of((String) auth.getPrincipal());
        }

        if (auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();

            return Optional.of(user.getUsername());
        }

        return Optional.empty();
    }

}
