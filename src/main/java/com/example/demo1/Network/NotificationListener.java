package com.example.demo1.Network;

import com.example.demo1.Model.ChatMessage;
import java.io.ObjectInputStream;

public class NotificationListener implements Runnable {
    private ObjectInputStream in;

    public NotificationListener(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            while (true) {
                NetworkPacket packet = (NetworkPacket) in.readObject();

                if (packet != null && packet.getRequestType() == RequestType.SEND_MESSAGE) {
                    ChatMessage incomingMsg = (ChatMessage) packet.getData();
                    System.out.println("new message from " + incomingMsg.getSenderId() + ": " + incomingMsg.getText());


                }
            }
        } catch (Exception e) {
            System.out.println("Notification listener stopped.");
        }
    }
}