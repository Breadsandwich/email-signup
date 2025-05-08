package com.email.signup.service;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class ValidationService {
    // Common disposable email domains
    private static final Set<String> DISPOSABLE_DOMAINS = Set.of(
        "tempmail.com", "throwawaymail.com", "mailinator.com", "guerrillamail.com",
        "10minutemail.com", "yopmail.com", "tempmail.net", "disposablemail.com"
    );

    // Strict email regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    // XSS prevention pattern
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "[<>\"'%;()&+]"
    );

    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Check email format
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return false;
        }

        // Check for disposable domains
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
        return !DISPOSABLE_DOMAINS.contains(domain);
    }

    public boolean containsXSS(String input) {
        return input != null && XSS_PATTERN.matcher(input).find();
    }

    public String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // Remove any potential XSS characters
        return input.replaceAll("[<>\"'%;()&+]", "");
    }

    public void validateSignupInput(String email, String name) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid or disposable email address");
        }

        if (containsXSS(name)) {
            throw new IllegalArgumentException("Name contains invalid characters");
        }
    }
}
