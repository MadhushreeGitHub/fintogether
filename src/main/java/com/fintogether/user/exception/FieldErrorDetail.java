package com.fintogether.user.exception;

public record FieldErrorDetail(String field, Object rejectedValue, String message) {
}
