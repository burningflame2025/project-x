package com.example.demo1.Network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println("=== Chat Server is starting on port " + PORT + " ===");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is ONLINE and waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected from: " + clientSocket.getRemoteSocketAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}