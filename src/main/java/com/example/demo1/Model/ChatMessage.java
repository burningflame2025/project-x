package com.example.demo1.Model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

public class ChatMessage implements Iterable<String> {
    private int id;
    private int senderId;
    private int receiverId;
    private String messageText;
    private Date sentDate;

    public ChatMessage(int id, int senderId, int receiverId, String messageText, Date sentDate) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.sentDate = sentDate;
    }

    public ChatMessage(int senderId, int receiverId, String messageText) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.sentDate = new Date();
    }

    // Getters و Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSenderId() { return senderId; }
    public int getReceiverId() { return receiverId; }

    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }

    public Date getSentDate() { return sentDate; }

    @Override
    public Iterator<String> iterator() {
        if (this.messageText == null || this.messageText.trim().isEmpty()) {
            return Collections.emptyIterator();
        }
        return Arrays.asList(this.messageText.split("\\s+")).iterator();
    }

    public String getText() {
        return this.messageText;
    }
}