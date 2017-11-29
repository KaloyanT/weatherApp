package com.weatherapp.util;

import org.apache.commons.validator.routines.EmailValidator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringVerifier {

    public static boolean validString(String str) {

        if((str == null) || str.isEmpty() || str.trim().length() == 0) {
            return false;
        }

        return true;
    }

    public static boolean validateUsername(String username) {
       final Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]{4,20}$");
       Matcher matcher = pattern.matcher(username);
       return matcher.find();
    }

    public static boolean validatePassword(String password) {
        final Pattern pattern = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20})");
        Matcher matcher = pattern.matcher(password);
        return matcher.find();
    }

    public static boolean validateEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean validateEmailRegEx(String email) {
        final Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.find();
    }
}
