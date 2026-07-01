package com.example.demo1.Controller;

import com.example.demo1.Model.*;
import java.util.ArrayList;
import java.util.List;

public class UserController {
    private Database db = Database.getInstance();

    public String rechargeTokens(int userId, int amount) {
        if (amount <= 0) return "Error: Amount must be positive!";
        User user = db.getUserById(userId);
        if (user == null) return "user not found";

        user.setTokens(user.getTokens() + amount);
        db.updateUser(user);
        return "Success: Recharged " + amount + " tokens. Current balance: " + user.getTokens();
    }

    public String upgradeSubscription(int userId, String newType) {
        User user = db.getUserById(userId);
        if (user == null) return "user not found";

        if (user.getSubscriptionType().name().equalsIgnoreCase(newType)) {
            return "Error: Already a " + newType + " user!";
        }

        int cost = newType.equalsIgnoreCase("gold") ? 20 : (newType.equalsIgnoreCase("blue") ? 10 : 0);
        if (user.getTokens() < cost) {
            return "Error: Not enough tokens! Need " + cost + ", but you have " + user.getTokens();
        }

        user.reduceTokens(cost);

        User upgradedUser = switch (newType.toLowerCase()) {
            case "blue" -> new BlueUser(user);
            case "gold" -> new GoldUser(user);
            default -> user;
        };

        db.updateUser(upgradedUser);
        return "Success: Upgraded to " + newType + " account!";
    }

    public String followUser(int followerId, int followingId) {
        if (followerId == followingId) return "cannot follow yourself";
        User follower = db.getUserById(followerId);
        User following = db.getUserById(followingId);
        if (follower == null || following == null) return "user not found";
        if (follower.getFollowing().contains(followingId)) return "already following";

        follower.followUser(followingId);
        following.addFollower(followerId);
        db.updateUser(follower);
        db.updateUser(following);
        return "following: " + following.getUsername();
    }

    public String unfollowUser (int followerId, int followingId) {
        User follower = db.getUserById(followerId);
        User following = db.getUserById(followingId);
        if (follower == null || following == null) return "user not found";
        if (!follower.getFollowing().contains(followingId)) return "not following";

        follower.unfollowUser(followingId);
        following.removeFollower(followerId);
        db.updateUser(follower);
        db.updateUser(following);
        return "unfollowed: " + following.getUsername();
    }

    public List<User> getFollowing(int userId){
        User user = db.getUserById(userId);
        List<User> result = new ArrayList<>();
        if (user != null) {
            for (int id : user.getFollowing()){
                User u = db.getUserById(id);
                if (u != null) result.add(u);
            }
        }
        return result;
    }
    public List<User> getFollowers(int userId) {
        User user = db.getUserById(userId);
        List<User> result = new ArrayList<>();
        if (user != null) {
            for (int id : user.getFollowers()) {
                User u = db.getUserById(id);
                if (u != null) result.add(u);
            }
        }
        return result;
    }

    public String editProfile(int userId, String bio, String firstName, String lastName, String email, String password) {

        User user = db.getUserById(userId);
        if (user == null) return "user not found";

        if (bio != null) user.setBio(bio);
        if (firstName != null && !firstName.trim().isEmpty()) user.setFirstName(firstName);
        if (lastName != null && !lastName.trim().isEmpty()) user.setLastName(lastName);
        if (email != null && !email.trim().isEmpty()) user.setEmail(email);
        if (password != null && !password.trim().isEmpty()) user.setPassword(password);

        db.updateUser(user);
        return "changes saved";
    }

    public User getUserInfo(int userId) {
        return db.getUserById(userId);
    }
    public List<Post> getUserPosts(int userId) {
        return db.getPostsByUserId(userId);
    }
    public String selectedHashtags (int userId, List<Integer> hashtagId) {
        if (hashtagId == null || hashtagId.isEmpty()) return "no hashtags selected";
        if (hashtagId.size() > 4) return "max 4 hashtags";
        User user = db.getUserById(userId);
        if (user == null) return "user not found";
        user.clearSelectedHashtags();
        for (int id : hashtagId) user.addSelectedHashtag(id);
        db.updateUser(user);
        return hashtagId.size() + " hashtags selected";
    }
    public String getUserStatus(int userId) {
        User user = db.getUserById(userId);
        if (user == null) return "user not found";
        return String.format(user.getUsername() + "%s\n" + "followers: %d | following: %d | posts: %d\n" +
                        "tokens: %d | subscription: %s", user.getSubscriptionBadge(), user.getFollowers().size(),
                user.getFollowing().size(), user.getPostIds().size(), user.getTokens(), user.getSubscriptionType());
    }
}