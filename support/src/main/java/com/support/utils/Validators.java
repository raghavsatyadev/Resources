package com.support.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

public class Validators {
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidMobile(String phone) {
        return phone.length() > 9 && Patterns.PHONE.matcher(phone).matches();
    }

    /**
     * Checks for 1 Uppercase Alphabet, 1 Number, 1 Special Character and at least 8 character length
     *
     * @param password String password to validate
     * @return returns true if password is in correct format
     */
    public static boolean isValidPassword(String password) {
        password = password.trim();
        if (!TextUtils.isEmpty(password)) {
            String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";

            return Pattern.compile(PASSWORD_PATTERN).matcher(password.trim()).matches() && password.length() >= 8;
        } else {
            return false;
        }

    }
}
