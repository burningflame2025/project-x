package com.example.demo1.Model;

//====admin====
public class Admin {
    private static Admin instance;
    private String username;
    private String password;

    private Admin() {
        this.username = "admin";
        this.password = "Admin@123";
    }

    public static Admin getInstance() {
        if (instance == null) {
            instance = new Admin();
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
}
