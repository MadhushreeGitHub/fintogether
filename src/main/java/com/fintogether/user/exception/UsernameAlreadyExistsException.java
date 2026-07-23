package com.fintogether.user.exception;

import lombok.Getter;

@Getter
public class UsernameAlreadyExistsException extends RuntimeException {
    private final String username;

    public UsernameAlreadyExistsException(String username) {
        super("This username is already taken");
        this.username = username;
    }
}
