package com.example.network;

import java.util.*;

import com.example.model.User;

public class SessionManager {
    private static final Map<String, User> sessions = new HashMap<>();

    public static void store(String token, User user) {
        sessions.put(token, user);
    }

    public static User get(String token) {
        return sessions.get(token);
    }

    public static boolean isValid(String token) {
        return sessions.containsKey(token);
    }
}

