package com.example;

import com.example.db.DatabaseInitializer;
import com.example.ClientHandler;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        DatabaseInitializer.initialize();
        System.out.println("Starting server on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());


                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            System.err.println("Server error:");
            e.printStackTrace();
        }
    }
}
