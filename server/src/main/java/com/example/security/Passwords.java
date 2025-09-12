package com.example.security;

import org.mindrot.jbcrypt.BCrypt;

public final class Passwords {
    private static final int COST = 12;

    public static String hash(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(COST));
    }

    public static boolean verify(String rawPassword, String storedHash) {
        if (storedHash == null || storedHash.isBlank()) return false;
        return BCrypt.checkpw(rawPassword, storedHash);
    }

    private Passwords() {}
}