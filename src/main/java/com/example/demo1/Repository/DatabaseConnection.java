package com.example.demo1.Repository;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3000/project_x"; // پورت و نام دیتابیس خودت را چک کن
    private static final String USERNAME = "root"; // نام کاربری دیتابیس شما
    private static final String PASSWORD = "TradeSystem123!"; // رمز عبور دیتابیس شما

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found!", e);
        }
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}