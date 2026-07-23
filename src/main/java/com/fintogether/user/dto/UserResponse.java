package com.fintogether.user.dto;

import com.fintogether.user.domain.Role;

import java.time.Instant;
import java.util.UUID;

/**
 * Response body for POST /api/v1/auth/signup.
 */
public record UserResponse (
        UUID id,
        String email,
        String phone,
        String username,
        Role role,
        Instant createdAt
){}
