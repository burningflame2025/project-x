package com.example.demo1.Repository;// امپورت مدل‌ها و کلاس‌های فرزند کاربر
import com.example.demo1.Model.BlueUser;
import com.example.demo1.Model.GoldUser;
import com.example.demo1.Model.NormalUser;
import com.example.demo1.Model.User;
import com.example.demo1.Repository.DatabaseConnection;
import com.example.demo1.interfaces.IRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IRepository<User> {

    @Override
    public void add(User user) throws Exception {
        String query = "INSERT INTO users (username, email, password, bio, user_type) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getBio());

            // تشخیص نوع کلاس فرزند برای ذخیره در دیتابیس
            String typeStr = "NORMAL";
            if (user instanceof BlueUser) {
                typeStr = "BLUE";
            } else if (user instanceof GoldUser) {
                typeStr = "GOLD";
            }
            stmt.setString(5, typeStr);

            stmt.executeUpdate();
        }
    }

    @Override
    public User findById(int id) throws Exception {
        String query = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // استفاده از متد کمکی برای نیو کردن کلاس فرزند درست
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<User> findAll() throws Exception {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        return users;
    }

    // --- متد کمکی برای حل مشکل ابسترکت بودن کلاس User ---
    private User mapUser(ResultSet rs) throws SQLException {
        String type = rs.getString("user_type");
        User user;

        // بر اساس تایپ ذخیره شده در دیتابیس، کلاس فرزند متناظر را نیو می‌کنیم و دیتای فاز اولت را اورراید یا مقداردهی می‌کنیم
        switch (type) {
            case "BLUE":
                user = new BlueUser(rs.getString("username"), rs.getString("email"), rs.getString("password"));
                break;
            case "GOLD":
                user = new GoldUser(rs.getString("username"), rs.getString("email"), rs.getString("password"));
                break;
            case "NORMAL":
            default:
                user = new NormalUser(rs.getString("username"), rs.getString("email"), rs.getString("password"), null, null, null);
                break;
        }

        // پر کردن سایر فیلدهای مشترک کلاس ابسترکت کامنت شده یا عمومی
        user.setId(rs.getInt("id")); // اگر متد setId در کلاس والد داری
        user.setBio(rs.getString("bio"));
        return user;
    }

    @Override
    public void delete(int id) throws Exception {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public void update(User user) throws Exception {
        String query = "UPDATE users SET bio = ?, password = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getBio());
            stmt.setString(2, user.getPassword());
            stmt.setInt(3, user.getId());
            stmt.executeUpdate();
        }
    }
}