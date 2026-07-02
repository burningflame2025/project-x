package com.example.demo1.Network;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private static ChatClient instance;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    private ChatClient() {
        try {
            this.socket = new Socket(SERVER_HOST, SERVER_PORT);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected to ChatServer successfully!");
        } catch (IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
        }
    }

    public static ChatClient getInstance() {
        if (instance == null) {
            instance = new ChatClient();
        }
        return instance;
    }

    public NetworkPacket sendRequest(NetworkPacket packet) {
        try {
            if (socket == null || socket.isClosed()) {
                return new NetworkPacket(packet.getRequestType(), null, false, "server disconnected");
            }
            out.writeObject(packet);
            out.flush();

            NetworkPacket response = (NetworkPacket) in.readObject();
            return response;

        } catch (Exception e) {
            return new NetworkPacket(packet.getRequestType(), null, false, "error sending/receiving data: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                sendRequest(new NetworkPacket(RequestType.DISCONNECT, null));
                if (in != null) in.close();
                if (out != null) out.close();
                socket.close();
                System.out.println("Disconnected from server cleanly.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}