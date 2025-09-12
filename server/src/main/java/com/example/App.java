package com.example;

import com.example.config.Beans;
import com.example.config.ServerConfig;
import com.example.db.DatabaseInitializer;
import com.example.server.RouteRegistrar;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class App {
    public static void main(String[] args) throws Exception {
        try { DatabaseInitializer.initialize(); }
        catch (Throwable t) { System.err.println("FATAL: DB init failed."); t.printStackTrace(); System.exit(1); }

        var cfg = ServerConfig.fromEnv();
        var beans = new Beans(cfg);

        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", cfg.port()), 0);
        server.setExecutor(Executors.newFixedThreadPool(cfg.threads()));

        RouteRegistrar.registerAll(server, beans);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> { System.out.println("\nShutting down..."); server.stop(1); }));
        System.out.println("HTTP server listening on http://localhost:" + cfg.port());
        server.start();
    }
}