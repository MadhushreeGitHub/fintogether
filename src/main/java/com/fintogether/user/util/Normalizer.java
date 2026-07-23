package com.fintogether.user.util;


public final class Normalizer {
    private Normalizer() { /* prevent instantiation */ }
    public static String email(String email) {
        if (email == null) {
            return null;
        }
        return email.toLowerCase().trim();
    }

    public static String phone(String phone) {
        if (phone == null) {
            return null;
        }
        return phone.replaceAll("[^0-9]", "");
    }
}
