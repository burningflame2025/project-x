package com.example.demo1.Model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

public class ChatMessage implements Iterable<String> {
    private int id;
    private int senderId;     // آیدی فرستنده پیام
    private int receiverId;   // آیدی گیرنده پیام
    private String messageText;
    private Date sentDate;    // زمان ارسال پیام

    // سازنده کامل (برای زمانی که داده رو از دیتابیس می‌خوانیم و آیدی دارد)
    public ChatMessage(int id, int senderId, int receiverId, String messageText, Date sentDate) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.sentDate = sentDate;
    }

    // سازنده فرعی (برای زمانی که کلاینت پیام جدید می‌سازد و هنوز آیدی دیتابیس ندارد)
    public ChatMessage(int senderId, int receiverId, String messageText) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.sentDate = new Date(); // زمان فعلی سیستم
    }

    // Getters و Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSenderId() { return senderId; }
    public int getReceiverId() { return receiverId; }

    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }

    public Date getSentDate() { return sentDate; }

    // پیاده‌سازی الزامی Iterable برای فاز ۲ (پیمایش روی کلمات پیام)
    @Override
    public Iterator<String> iterator() {
        if (this.messageText == null || this.messageText.trim().isEmpty()) {
            return Collections.emptyIterator();
        }
        // شکستن متن پیام به کلمات (بر اساس فاصله‌ها) برای استفاده در حلقه foreach
        return Arrays.asList(this.messageText.split("\\s+")).iterator();
    }
}