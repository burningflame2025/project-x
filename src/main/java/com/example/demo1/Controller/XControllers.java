package com.example.demo1.Controller;

import com.example.demo1.Model.*;
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
            if (db.getUserByUsername(cleanUsername) != null) return "Error: Username exists!";
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

            db.addUser(newUser);
            String successMessage = "Success: " + type + " account created!";

            List<Hashtag> popular = db.getPopularHashtags();
            StringBuilder hs = new StringBuilder();
            for (int i = 0; i < Math.min(4, popular.size()); i++)
                hs.append("\n   ").append(i+1).append(". ").append(popular.get(i).getTitle());
            return successMessage + "\npopular hashtags: " + hs;
        }
        public User login(String username, String password) throws Exception {
            User user = db.getUserByUsername(username);

            if (user == null) {
                throw new Exception("USER_NOT_FOUND");
            }
            if (!user.getPassword().equals(password)) {
                throw new Exception("WRONG_PASSWORD");
            }
            if (user.isLocked()) {
                throw new Exception("ACCOUNT_LOCKED");
            }

            return user;
        }

        public boolean adminLogin(String username, String password) {
            Admin admin = db.getAdmin();
            return admin.getUsername().equals(username) && admin.getPassword().equals(password);
        }
    }
}