package com.example.demo1.Model;

import java.util.ArrayList;
import java.util.List;

//========hashtag==========
public class Hashtag {
    private static int nextId = 1;
    private int id;
    private String title;
    private List<Integer> postIds;
    private int usageCount;

    public Hashtag(String title) {
        this.id = nextId++;
        this.title = title.startsWith("#") ? title : "#" + title;
        this.postIds = new ArrayList<>();
        this.usageCount = 0;
    }

    public void addPost(int postId) {
        if (!postIds.contains(postId)) {
            postIds.add(postId);
            usageCount++;
        }
    }

    public int getUsageCount() {
        return usageCount;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<Integer> getPostIds() {
        return new ArrayList<>(postIds);
    }
}
