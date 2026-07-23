package com.fintogether.user.exception;

import lombok.Getter;

@Getter
public class PhoneAlreadyExistsException extends RuntimeException{

    private final String phone;
    public PhoneAlreadyExistsException(String phone) {
        super("An account with this phone number already exists");
        this.phone = phone;
    }
}
