package com.example;

import com.example.db.DatabaseInitializer;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initialize();

        System.out.println("✅ The server id ready to start!");

        // TODO: Add Socket logic:
    }
}
