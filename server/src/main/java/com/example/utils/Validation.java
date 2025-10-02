package com.example.utils;
import com.example.exception.BadRequestException;

import java.util.Set;
import java.util.regex.Pattern;

public class Validation {
    public static String requireNonBlank(String field, String value, int min, int max) {
        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestException("invalid_" + field, field + " is required");
        }
        String trimmed = value.trim();
        if (trimmed.length() < min) {
            throw new BadRequestException("invalid_" + field, field + " too_short");
        }
        if (trimmed.length() > max) {
            throw new BadRequestException("invalid_" + field, field + " too_long");
        }
        return trimmed;
    }

    public static String optionalLength(String field, String value, int min, int max) {
        if (value == null) return null;
        String trimmed = value.trim();
        if (trimmed.isEmpty()) return null;
        if (trimmed.length() < min) {
            throw new BadRequestException("invalid_" + field, field + " too_short");
        }
        if (trimmed.length() > max) {
            throw new BadRequestException("invalid_" + field, field + " too_long");
        }
        return trimmed;
    }

    public static String requirePattern(String field, String value, String regex) {
        if (!Pattern.matches(regex, value)) {
            throw new BadRequestException("invalid_" + field, field + " invalid");
        }
        return value;
    }
}

