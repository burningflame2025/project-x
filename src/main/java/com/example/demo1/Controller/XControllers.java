package com.example.demo1.Controller;

import com.example.demo1.Model.*;
import com.example.demo1.Network.ChatClient;
import com.example.demo1.Network.NetworkPacket;
import com.example.demo1.Network.RequestType;

import java.util.regex.Pattern;
import java.util.List;

public class XControllers {
    public static class AuthController {
        private Database db = Database.getInstance();

        public boolean isValidEmail(String email) {
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            return Pattern.matches(emailRegex, email);
        }
        public boolean isValidPhone(String phone) {
            String phoneRegex = "^09\\d{9}$";
            return Pattern.matches(phoneRegex, phone);
        }
        public boolean isValidPassword(String password) {
            String passRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
            return Pattern.matches(passRegex, password);
        }

        public String register(String username, String password, String email, String phone, String firstName, String lastname, String accountType) {
            String cleanUsername = (username != null) ? username.trim() : "";
            if (cleanUsername.isEmpty()) return "Error: Username cannot be empty!";
            if (!isValidEmail(email)) return "Error: Invalid email format!";
            if (!isValidPhone(phone)) return "Error: Invalid phone format!";
            if (!isValidPassword(password))
                return "Error: Password must be at least 8 chars with upper, lower & numbers.";

            String type = (accountType != null) ? accountType.toLowerCase() : "normal";
            User newUser = switch (type) {
                case "blue" -> new BlueUser(cleanUsername, password, email);
                case "gold" -> new GoldUser(cleanUsername, password, email);
                default -> new NormalUser(cleanUsername, password, email, phone, firstName, lastname);
            };

            try {
                NetworkPacket registerPacket = new NetworkPacket(RequestType.REGISTER, newUser);
                NetworkPacket response = ChatClient.getInstance().sendRequest(registerPacket);

                if (!response.isSuccess()) {
                    return "Error: " + response.getStatusMessage();
                }

                return "Success: " + type + " account created!";

            } catch (Exception e) {
                return "Error: Connection to server failed! " + e.getMessage();
            }
        }

        public User login(String username, String password) throws Exception {
            String[] credentials = new String[]{username, password};
            NetworkPacket loginPacket = new NetworkPacket(RequestType.LOGIN, credentials);
            NetworkPacket response = ChatClient.getInstance().sendRequest(loginPacket);

            if (!response.isSuccess()) {
                throw new Exception(response.getStatusMessage());
            }
            return (User) response.getData();
        }
        public boolean adminLogin(String username, String password) {
            Admin admin = db.getAdmin();
            return admin.getUsername().equals(username) && admin.getPassword().equals(password);
        }
    }
}