package com.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class PizzaClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Connected to server!");

            String welcomeMsg = in.readLine();
            System.out.println("Server: " + welcomeMsg);

            String input;
            while (true) {
                System.out.print("You: ");
                input = scanner.nextLine();
                out.println(input);

                if (input.equalsIgnoreCase("exit")) break;

                String response = in.readLine();
                System.out.println("Server: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
