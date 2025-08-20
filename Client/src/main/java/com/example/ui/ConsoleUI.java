package com.example.ui;

import java.io.Console;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner in = new Scanner(System.in);

    public String prompt(String label) {
        System.out.print(label + ": ");
        return in.nextLine().trim();
    }

    public String promptPassword(String label) {
        Console console = System.console();
        if (console != null) {
            char[] pwd = console.readPassword(label + ": ");
            return pwd == null ? "" : new String(pwd);
        }
        System.out.print(label + ": ");
        return in.nextLine();
    }

    public int choose(String title, List<String> options, int defIndex) {
        System.out.println();
        System.out.println(title);
        for (int i = 0; i < options.size(); i++) {
            System.out.printf("%d) %s%n", i + 1, options.get(i));
        }
        System.out.printf("Choice [%d]: ", defIndex + 1);
        try { return Integer.parseInt(in.nextLine()) - 1; } catch (Exception e) { return defIndex; }
    }

    public void info(String msg) { System.out.println(msg); }
    public void error(String msg) { System.out.println("Error: " + msg); }
}