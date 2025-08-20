package com.example.flows;

import com.example.client.ApiClient;
import com.example.client.ApiException;
import com.example.core.Session;
import com.example.dto.AuthDtos;
import com.example.ui.ConsoleUI;

import java.util.List;

public class AuthFlow {
    private final ApiClient api; private final Session session; private final ConsoleUI ui;

    public AuthFlow(ApiClient api, Session session, ConsoleUI ui) {
        this.api = api; this.session = session; this.ui = ui;
    }

    public void run() {
        while (true) {
            int c = ui.choose("Welcome", List.of(
                    "Login",
                    "Register",
                    "Exit"), 0);
            switch (c) {
                case 0 -> login();
                case 1 -> register();
                case 2 -> System.exit(0);
                default -> {}
            }
        }
    }

    private void login() {
        String username = ui.prompt("Username");
        String pass = ui.promptPassword("Password");
        try {
            AuthDtos.AuthResult res = api.login(username, pass);
            if (res != null && res.token != null && res.username != null) {
                session.login(res.token, res.username);
                ui.info("Signed in as " + res.username);
                return;
            }
            ui.error("Unexpected login response.");
        } catch (ApiException ex) {
            handleAuthError(ex);
        }
    }

    private void register() {
        String fullName = ui.prompt("Full Name");
        String username = ui.prompt("Username");
        String pass = ui.promptPassword("Password");
        try {
            AuthDtos.AuthResult res = api.register(fullName, username, pass);
            if (res != null && res.token != null && res.username != null) {
                session.login(res.token, res.username);
                ui.info("Registered and signed in as " + res.username);
                return;
            }
            ui.error("Unexpected register response.");
        } catch (ApiException ex) {
            handleAuthError(ex);
        }
    }

    private void handleAuthError(ApiException ex) {
        if (ex.getStatus() == 401) ui.error("Invalid credentials.");
        else if (ex.getStatus() == 409) ui.error("Conflict (already exists?).");
        else ui.error(ex.getMessage());
    }
}