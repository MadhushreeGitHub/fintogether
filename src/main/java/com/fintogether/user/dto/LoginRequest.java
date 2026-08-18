package com.fintogether.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for POST /api/v1/auth/signup.
 */

public record LoginRequest (
    @NotBlank
    @Email
    @Size(max=254)
    String email,

    @NotBlank @Size(min = 8, max = 128)
    String password
) {};
