package com.example.demo1.Network;

import com.example.demo1.Model.*;
import com.example.demo1.Repository.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private UserRepository userRepository = new UserRepository();
    private PostRepository postRepository = new PostRepository();
    private MessageRepository messageRepository = new MessageRepository();

    private static final java.util.List<ClientHandler> activeClients = new java.util.concurrent.CopyOnWriteArrayList<>();
    private User loggedInUser;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            activeClients.add(this);
            while (true) {
                NetworkPacket request = (NetworkPacket) in.readObject();
                if (request == null) break;

                NetworkPacket response = handleRequest(request);
                out.writeObject(response);
                out.flush();

                if (request.getRequestType() == RequestType.DISCONNECT) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Client disconnected with error: " + e.getMessage());
        } finally {
            closeConnection();
            activeClients.remove(this);
        }
    }

    private NetworkPacket handleRequest(NetworkPacket request) {
        try {
            switch (request.getRequestType()) {
                case LOGIN:
                    String[] credentials = (String[]) request.getData();
                    String username = credentials[0];
                    String password = credentials[1];
                    User foundUser = userRepository.findByUsername(username);

                    if (foundUser == null) {
                        return new NetworkPacket(RequestType.LOGIN, null, false, "User not found!");
                    } else if (!foundUser.getPassword().equals(password)) {
                        return new NetworkPacket(RequestType.LOGIN, null, false, "Wrong password!");
                    } else if (foundUser.isLocked()) {
                        return new NetworkPacket(RequestType.LOGIN, null, false, "This account is suspended!");
                    }

                    return new NetworkPacket(RequestType.LOGIN, foundUser, true, "Success");

                case REGISTER:
                    User newUser = (User) request.getData();
                    if (userRepository.findByUsername(newUser.getUsername()) != null) {
                        return new NetworkPacket(RequestType.REGISTER, null, false, "Username already exists!");
                    } else {
                        userRepository.add(newUser);
                        return new NetworkPacket(RequestType.REGISTER, null, true, "Registration Successful");
                    }
                case SEND_MESSAGE:
                    ChatMessage msg = (ChatMessage) request.getData();
                    messageRepository.add(msg);

                    for (ClientHandler client : activeClients) {
                        if (client.loggedInUser != null && client.loggedInUser.getId() == msg.getReceiverId()) {
                            try {
                                client.out.writeObject(new NetworkPacket(RequestType.SEND_MESSAGE, msg, true, "New Live Message"));
                                client.out.flush();
                            } catch (IOException e) {
                                System.out.println("Failed to send live message to user " + msg.getReceiverId());
                            }
                            break;
                        }
                    }
                    return new NetworkPacket(RequestType.SEND_MESSAGE, msg, true, "message sent.");
                case SYNC_HISTORY:

                    int[] userIds = (int[]) request.getData();
                    List<ChatMessage> history = messageRepository.getChatHistory(userIds[0], userIds[1]);
                    return new NetworkPacket(RequestType.SYNC_HISTORY, history, true, "history synced.");

                case CREATE_POST:
                    Post post = (Post) request.getData();
                    postRepository.add(post);
                    return new NetworkPacket(RequestType.CREATE_POST, post, true, "post uploaded.");


                default:
                    return new NetworkPacket(request.getRequestType(), null, false, "unknown request");
            }
        } catch (Exception e) {
            return new NetworkPacket(request.getRequestType(), null, false, "server error: " + e.getMessage());
        }
    }

    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("Connection closed clean.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}