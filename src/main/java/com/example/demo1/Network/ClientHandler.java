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

                // اگر کلاینت درخواست دیسکنکت داد، حلقه را می‌شکنیم
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
                    User loginData = (User) request.getData();
                    User foundUser = userRepository.findById(loginData.getId());
                    if (foundUser != null && foundUser.getPassword().equals(loginData.getPassword())) {
                        this.loggedInUser = foundUser;
                        return new NetworkPacket(RequestType.LOGIN, foundUser, true, "welcome");
                    }
                    return new NetworkPacket(RequestType.LOGIN, null, false, "wrong password or username.");

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