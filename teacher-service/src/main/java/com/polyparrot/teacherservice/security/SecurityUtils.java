package com.polyparrot.teacherservice.security;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static AuthenticatedUser getCurrentUser() {
        return (AuthenticatedUser)
                SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
