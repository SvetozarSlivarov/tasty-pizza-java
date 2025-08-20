package com.example.dto;


public class AuthDtos {
    public static class LoginRequest {
        public String username;
        public String password;
        public LoginRequest() {}
        public LoginRequest(String username, String password) {
            this.username=username;
            this.password=password;
        }
    }
    public static class RegisterRequest {
        public String fullname;
        public String password;
        public String username;
        public RegisterRequest() {}
        public RegisterRequest(String fullname, String username, String password) {
            this.fullname = fullname;
            this.password = password;
            this.username = username;
        }
    }

    public static class AuthResult {
        public String username;
        public String token;
    }
}