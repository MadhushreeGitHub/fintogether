package com.fintogether.user.exception;

import lombok.Getter;

@Getter
public class EmailAlreadyExistsException extends RuntimeException {
    private final String email;

    public EmailAlreadyExistsException(String email) {
        super("An account with this email already exists");
        this.email = email;
    }
}
