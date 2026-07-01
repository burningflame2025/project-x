package com.example.demo1.Controller;

import com.example.demo1.Model.*;
import java.util.List;

public class AdminController {
    private Database db = Database.getInstance();

    public String confirmReport(int reportId) {
        Report report = db.getReportById(reportId);
        if (report == null) return "ERROR: Report not found!";
        if (report.getStatus() != ReportStatus.WAITING) return "ERROR: Already processed!";

        report.setStatus(ReportStatus.CONFIRMED);

        if (report.getReportType().equals("POST")) {
            Post post = db.getPostById(report.getContentId());
            if (post != null) {
                post.setLocked(true);
                return "SUCCESS: Report confirmed! Post #" + post.getId() + " has been locked.";
            }
        } else if (report.getReportType().equals("USER")) {
            User user = db.getUserById(report.getReportedUserId());
            if (user != null) {
                user.setLocked(true);
                db.updateUser(user);
                return "SUCCESS: Report confirmed! User @" + user.getUsername() + " has been locked.";
            }
        }

        return "SUCCESS: Report confirmed!";
    }

    public String rejectReport(int reportId) {
        Report report = db.getReportById(reportId);
        if (report == null) return "ERROR: Report not found!";
        if (report.getStatus() != ReportStatus.WAITING) return "ERROR: Already processed!";

        report.setStatus(ReportStatus.REJECTED);
        return "SUCCESS: Report rejected!";
    }

    public List<User> getAllUsers() {
        return db.getUsers();
    }

    public String lockUser(int userId) {
        User user = db.getUserById(userId);
        if (user == null) {
            return "ERROR: User not found!";
        }
        if (user.isLocked()) {
            return "ERROR: User is already locked!";
        }

        user.setLocked(true);
        db.updateUser(user);
        return "SUCCESS: User @" + user.getUsername() + " has been locked!";
    }

    public String unlockUser(int userId) {
        User user = db.getUserById(userId);
        if (user == null) {
            return "ERROR: User not found!";
        }
        if (!user.isLocked()) {
            return "ERROR: User is not locked!";
        }

        user.setLocked(false);
        db.updateUser(user);
        return "SUCCESS: User @" + user.getUsername() + " has been unlocked!";
    }

    public List<Post> getAllPosts() {
        return db.getAllPosts();
    }

    public String lockPost(int postId) {
        Post post = db.getPostById(postId);
        if (post == null) {
            return "ERROR: Post not found!";
        }
        if (post.isLocked()) {
            return "ERROR: Post is already locked!";
        }

        post.setLocked(true);
        return "SUCCESS: Post #" + postId + " has been locked!";
    }

    public String unlockPost(int postId) {
        Post post = db.getPostById(postId);
        if (post == null) {
            return "ERROR: Post not found!";
        }
        if (!post.isLocked()) {
            return "ERROR: Post is not locked!";
        }

        post.setLocked(false);
        return "SUCCESS: Post #" + postId + " has been unlocked!";
    }

    public List<Post> getPopularPosts(int limit) {
        return db.getPopularPosts(limit);
    }

    public List<Hashtag> getPopularHashtags() {
        return db.getPopularHashtags();
    }
}