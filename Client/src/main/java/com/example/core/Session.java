package com.example.core;

public class Session {
    private String token;
    private String username;

    public boolean isAuthenticated() { return token != null; }

    public void login(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public void logout() {
        token = null; username = null;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
}