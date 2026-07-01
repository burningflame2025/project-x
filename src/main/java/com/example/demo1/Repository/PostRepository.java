package com.example.demo1.Repository;

import com.example.demo1.Interfaces.IRepository;
import com.example.demo1.Model.Post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PostRepository implements IRepository<Post> {

    @Override
    public void add(Post post) throws Exception {
        String query = "INSERT INTO posts (author_id, post_text, parent_post_id, repost_of_id, views) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, post.getAuthorId());
            stmt.setString(2, post.getText());
            stmt.setObject(3, post.getParentPostId()); // استفاده از setObject برای مقادیر Null ایمن است
            stmt.setObject(4, post.getRepostOfId());
            stmt.setInt(5, post.getViews());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Post> findAll() throws Exception {
        List<Post> posts = new ArrayList<>();
        String query = "SELECT * FROM posts WHERE is_deleted = FALSE ORDER BY created_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Post post = new Post(
                        rs.getString("post_text"),
                        rs.getInt("author_id"),
                        null, // مدیا را اگر فاز اول هندل کردی مسیرش را بفرست
                        (Integer) rs.getObject("parent_post_id")
                );
                post.setRepostOfId((Integer) rs.getObject("repost_of_id"));
                posts.add(post);
            }
        }
        return posts;
    }

    @Override public void delete(int id) throws Exception {}
    @Override public void update(Post item) throws Exception {}
    @Override public Post findById(int id) throws Exception { return null; }
}