package com.polyparrot.chatservice.security;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static AuthenticatedUser getCurrentUser() {

        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof AuthenticatedUser)) {
            throw new RuntimeException("User not authenticated");
        }

        return (AuthenticatedUser) principal;
    }
}