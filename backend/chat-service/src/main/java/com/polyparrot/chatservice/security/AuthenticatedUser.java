package com.polyparrot.chatservice.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticatedUser {
    private Long userId;
    private String email;
    private String role;
}