package com.example.security;

import java.util.Map; import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TokenService {
    private final Map<String,String> tokenToUser = new ConcurrentHashMap<>();
    public String issueToken(String username){ String t=UUID.randomUUID().toString(); tokenToUser.put(t, username); return t; }
    public String resolveUser(String token){ return tokenToUser.get(token); }
}
