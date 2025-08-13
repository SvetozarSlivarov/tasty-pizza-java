package com.example.network;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final ObjectMapper mapper = new ObjectMapper();

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String input;
            while ((input = in.readLine()) != null) {
                try {
                    JsonNode request = mapper.readTree(input);

                    if (!request.has("action") || !request.has("payload")) {
                        out.println("{\"error\":\"Missing required fields\"}");
                        continue;
                    }

                    String action = request.get("action").asText();
                    JsonNode payload = request.get("payload");

                    JsonNode response = RouteDispatcher.dispatch(action, payload);
                    out.println(mapper.writeValueAsString(response));

                } catch (Exception e) {
                    out.println("{\"error\":\"Invalid JSON format\"}");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { clientSocket.close(); } catch (IOException ignored) {}
        }
    }
}
