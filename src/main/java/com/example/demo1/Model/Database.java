package com.example.demo1.Model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

//=======database======
public class Database {

    private static Database instance;

    private final List<User> users;
    private final List<Post> posts;
    private final List<Hashtag> hashtags;
    private final List<Report> reports;

    private final Admin admin;

    private Database() {

        users = new ArrayList<>();
        posts = new ArrayList<>();
        hashtags = new ArrayList<>();
        reports = new ArrayList<>();

        admin = Admin.getInstance();

        hashtags.add(new Hashtag("#News"));
        hashtags.add(new Hashtag("#Sports"));
        hashtags.add(new Hashtag("#Education"));
        hashtags.add(new Hashtag("#Iran"));
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    //================ USER =================

    public void addUser(User user) {
        if (user != null) {
            users.add(user);
        }
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public User getUserById(int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    public User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public List<User> getLockedUsers() {

        List<User> lockedUsers = new ArrayList<>();

        for (User user : users) {
            if (user.isLocked()) {
                lockedUsers.add(user);
            }
        }

        return lockedUsers;
    }

    public void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {

                users.set(i, user);
                return;
            }
        }
    }

    //================ POSTS =================

    public void addPost(Post post) {
        if (post != null) {
            posts.add(post);
        }
    }

    public void removePost(Post post) {
        posts.remove(post);
    }

    public Post getPostById(int id) {
        for (Post post : posts) {
            if (post.getId() == id) {
                return post;
            }
        }

        return null;
    }

    public List<Post> getPostsByUserId(int userId) {
        List<Post> result = new ArrayList<>();
            for (Post post : posts) {
            if (post.getAuthorId() == userId
                    && !post.isDeleted()
                    && !post.isLocked()) {
                result.add(post);
            }
        }
        return result;
    }

    public List<Post> getPostsByHashtagId(int hashtagId) {
        Hashtag hashtag = getHashtagById(hashtagId);
        if (hashtag == null) {
            return new ArrayList<>();
        }
        List<Post> result = new ArrayList<>();
        for (int postId : hashtag.getPostIds()) {
            Post post = getPostById(postId);
            if (post != null
                    && !post.isDeleted()
                    && !post.isLocked()) {
                result.add(post);
            }
        }
        return result;
    }

    public List<Post> getAllPosts() {
        List<Post> result = new ArrayList<>();
        for (Post post : posts) {
            if (!post.isDeleted() && !post.isLocked()) {
                result.add(post);
            }
        }
        return result;
    }

    public List<Post> getPopularPosts(int limit) {
        List<Post> popularPosts = getAllPosts();
        popularPosts.sort(Comparator.comparingInt(Post::getLikeCount).reversed()
        );
        return new ArrayList<>(
                popularPosts.subList(0, Math.min(limit, popularPosts.size())
                )
        );
    }

    //================ HASHTAGS =================
    public void addHashtag(Hashtag hashtag) {
        if (hashtag != null) {
            hashtags.add(hashtag);
        }
    }
    public Hashtag getHashtagById(int id) {
        for (Hashtag hashtag : hashtags) {
            if (hashtag.getId() == id) {
                return hashtag;
            }
        }
        return null;
    }

    public Hashtag getHashtagByTitle(String title) {
        String formatted = title.startsWith("#") ? title : "#" + title;
        for (Hashtag hashtag : hashtags) {
            if (hashtag.getTitle().equalsIgnoreCase(formatted)) {
                return hashtag;
            }
        }
        return null;
    }

    public Hashtag getOrCreateHashtag(String title) {
        Hashtag hashtag = getHashtagByTitle(title);
        if (hashtag != null) {
            return hashtag;
        }
        hashtag = new Hashtag(title);
        hashtags.add(hashtag);
        return hashtag;
    }

    public List<Hashtag> getHashtags() {
        return new ArrayList<>(hashtags);
    }

    public List<Hashtag> getPopularHashtags() {
        List<Hashtag> result = new ArrayList<>(hashtags);
        result.sort(Comparator.comparingInt(Hashtag::getUsageCount).reversed()
        );
        return result;
    }

    //================ REPORTS =================

    public void addReport(Report report) {
        if (report != null) {
            reports.add(report);
        }
    }

    public Report getReportById(int id) {
        for (Report report : reports) {
            if (report.getId() == id) {
                return report;
            }
        }
        return null;
    }

    public List<Report> getReportsByStatus(ReportStatus status) {
        List<Report> result = new ArrayList<>();
        for (Report report : reports) {
            if (report.getStatus() == status) {
                result.add(report);
            }
        }
        return result;
    }

    public List<Report> getAllReports() {
        return new ArrayList<>(reports);
    }
    //================ ADMIN =================

    public Admin getAdmin() {
        return admin;
    }
    //================ TOKENS =================
    public void dailyTokenRecharge() {
        for (User user : users) {
            int minimumTokens;
            if (user instanceof GoldUser) {
                minimumTokens = 600;
            }
            else if (user instanceof BlueUser) {
                minimumTokens = 400;
            }
            else {
                minimumTokens = 250;
            }
            if (user.getTokens() < minimumTokens) {
                user.setTokens(minimumTokens);
            }
        }
    }
}