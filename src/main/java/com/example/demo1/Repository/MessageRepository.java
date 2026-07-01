package com.example.demo1.Repository;

import com.example.demo1.Interfaces.IRepository;
import com.example.demo1.Model.ChatMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository implements IRepository<ChatMessage> {

    @Override
    public void add(ChatMessage msg) throws Exception {
        String query = "INSERT INTO chat_messages (sender_id, receiver_id, message_text) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, msg.getSenderId());
            stmt.setInt(2, msg.getReceiverId());
            stmt.setString(3, msg.getMessageText());
            stmt.executeUpdate();
        }
    }

    // متد اختصاصی فاز دوم: گرفتن تاریخچه چت بین دو کاربر مشخص
    public List<ChatMessage> getChatHistory(int user1, int user2) throws Exception {
        List<ChatMessage> history = new ArrayList<>();
        String query = "SELECT * FROM chat_messages WHERE (sender_id = ? AND receiver_id = ?) " +
                "OR (sender_id = ? AND receiver_id = ?) ORDER BY sent_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user1);
            stmt.setInt(2, user2);
            stmt.setInt(3, user2);
            stmt.setInt(4, user1);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    history.add(new ChatMessage(
                            rs.getInt("id"),
                            rs.getInt("sender_id"),
                            rs.getInt("receiver_id"),
                            rs.getString("message_text"),
                            rs.getTimestamp("sent_date")
                    ));
                }
            }
        }
        return history;
    }

    // متدهای اجباری اینترفیس را به صورت خلوت رها کن یا پیاده‌سازی ساده بنویس
    @Override public void delete(int id) throws Exception {}
    @Override public void update(ChatMessage item) throws Exception {}
    @Override public List<ChatMessage> findAll() throws Exception { return null; }
    @Override public ChatMessage findById(int id) throws Exception { return null; }
}