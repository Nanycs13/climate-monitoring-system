package com.example.climatemonitoring.utils;

public class EmailValidatorUtil {
    private static final String EMAIL_REGEX = "^(?=.{1,64}@.{3,255}$)(?=.{6,320}$)[a-zA-Z0-9](?!.*\\.\\.)[a-zA-Z0-9._%+-]{0,62}[a-zA-Z0-9]@[a-zA-Z0-9](?!.*\\.\\.)[a-zA-Z0-9.-]{0,253}[a-zA-Z0-9]\\.[a-zA-Z]{2,}$";

    public static boolean isEmailValido(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }
}
