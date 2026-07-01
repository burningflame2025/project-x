package com.example.demo1.Controller;

import com.example.demo1.Model.Database;
import com.example.demo1.Model.Hashtag;
import com.example.demo1.Model.Post;
import com.example.demo1.Model.User;

import java.util.ArrayList;
import java.util.List;

public class SearchController {
    private Database db = Database.getInstance();

    public List<User> searchByUsername(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return new ArrayList<>();
        List<User> result = new ArrayList<>();
        for (User u : db.getUsers()) {
            if (u.getUsername().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(u);
            }
        }
        return result;
    }

    public List<Post> searchInPosts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return new ArrayList<>();
        List<Post> result = new ArrayList<>();
        for (Post p : db.getAllPosts()) {
            if (p.getText().toLowerCase().contains(keyword.toLowerCase())) result.add(p);
        }
        return result;
    }

    public List<Post> searchByHashtag(String hashtagTitle) {
        Hashtag h = db.getHashtagByTitle(hashtagTitle);
        if (h == null) return new ArrayList<>();
        return db.getPostsByHashtagId(h.getId());
    }
}
