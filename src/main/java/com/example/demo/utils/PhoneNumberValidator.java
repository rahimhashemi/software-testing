package com.example.demo.utils;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class PhoneNumberValidator {
    String regex = "^\\+\\d{12}$";
    private Pattern pattern = Pattern.compile(regex);

    public boolean validate(String phoneNumber) {

        return phoneNumber != null && !phoneNumber.isBlank() && pattern.matcher(phoneNumber).matches()
                && phoneNumber.startsWith("+98");
    }
}
