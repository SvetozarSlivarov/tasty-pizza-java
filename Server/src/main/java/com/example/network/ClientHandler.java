package com.example.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String input;
            while ((input = in.readLine()) != null) {
                JsonObject request = JsonParser.parseString(input).getAsJsonObject();
                String action = request.get("action").getAsString();
                JsonObject payload = request.getAsJsonObject("payload");

                JsonObject response = RouteDispatcher.dispatch(action, payload);
                out.println(response.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
