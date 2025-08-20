package com.example;

import com.example.client.ApiClient;
import com.example.core.Session;
import com.example.flows.AuthFlow;
import com.example.ui.ConsoleUI;

public class App {
    public static void main(String[] args) {
        String base = System.getenv().getOrDefault("API_BASE_URL", "http://localhost:8080");
        var api = new ApiClient(base);
        var session = new Session();
        var ui = new ConsoleUI();

        while (true) {
            if (!session.isAuthenticated()) {
                new AuthFlow(api, session, ui).run();
                continue;
            }
            ui.info("(TODO) Main menu after login — e.g., CustomerFlow/AdminFlow");
            ui.info("Logging out to repeat the auth flow...");
            session.logout();
        }
    }
}