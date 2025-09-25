package com.example.dto.auth;
public class AuthResponse {
    public String username;
    public String token;
    public AuthResponse(){}
    public AuthResponse(String username, String token){
        this.username = username;
        this.token = token;
    }
}