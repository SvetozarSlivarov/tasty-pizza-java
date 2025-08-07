package com.example;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(
                        clientSocket.getOutputStream(), true)
        ) {
            out.println("Hello! You are connected to TastyPizza Server!");

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("🗨Received: " + inputLine);

                if (inputLine.equalsIgnoreCase("exit")) {
                    out.println("Goodbye!");
                    break;
                }

                out.println("Echo: " + inputLine);
            }

        } catch (IOException e) {
            System.err.println("Client connection error:");
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client disconnected.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
