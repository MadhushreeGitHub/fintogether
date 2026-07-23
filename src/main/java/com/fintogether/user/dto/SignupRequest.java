package com.fintogether.user.dto;


import com.fintogether.user.domain.Role;
import jakarta.validation.constraints.*;

/**
 * Request body for POST /api/v1/auth/signup.
 */

public record SignupRequest(
        @NotBlank @Email @Size(max=254)
        String email,

        @NotBlank @Size(max = 16)
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "must be a valid E.164 phone number")
        String phone,

        @NotBlank @Size(min=3,max=50)
        String username,

        @NotBlank @Size(min = 8, max = 128)
        String password,

        @NotNull
        Role role

) { }