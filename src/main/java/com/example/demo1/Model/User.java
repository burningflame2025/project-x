package com.example.demo1.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//========user=========
public abstract class User implements Serializable {
    private static int nextId = 1;

    int id;
    private Date createdDate;

    private String username;
    private String password;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String bio;
    private int tokens;
    private boolean isLocked;
    private int reportCount;
    private Date birthDate;
    private String profileCover;

    private ArrayList<Integer> followers;
    private ArrayList<Integer> followings;
    private ArrayList<Integer> postIds;
    private ArrayList<Integer> selectedHashtagIds;

    public User(String username, String password, String email, String phone, String firstName, String lastName) {
        this.id = nextId++;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdDate = new Date();
        this.bio = "";
        this.tokens = 0;
        this.reportCount = 0;
        this.isLocked = false;
        this.followers = new ArrayList<>();
        this.followings = new ArrayList<>();
        this.postIds = new ArrayList<>();
        this.selectedHashtagIds = new ArrayList<>();
    }


    protected User(User other) {
        this.id = other.id;
        this.username = other.username;
        this.password = other.password;
        this.email = other.email;
        this.phone = other.phone;
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.bio = other.bio;
        this.tokens = other.tokens;
        this.isLocked = other.isLocked;
        this.reportCount = other.reportCount;
        this.createdDate = other.createdDate;
        this.followers = new ArrayList<>(other.followers);
        this.followings = new ArrayList<>(other.followings);
        this.postIds = new ArrayList<>(other.postIds);
        this.selectedHashtagIds = new ArrayList<>(other.selectedHashtagIds);
    }

    public abstract int calculatePostCost(String text, boolean hasMedis);

    public abstract int getDailyTokenRecharge();

    public abstract String getSubscriptionBadge();

    public abstract SubscriptionType getSubscriptionType();

    public boolean reduceTokens(int amount) {
        if (this.tokens >= amount) {
            this.tokens -= amount;
            return true;
        } else {
            return false;
        }
    }

    public void addTokens(int amount) {
        this.tokens += amount;
    }

    public void followUser(int userId) {
        if (!this.followings.contains(userId) && this.id != userId) {
            this.followings.add(userId);
        }
    }

    public void unfollowUser(int userId) {
        this.followings.remove(Integer.valueOf(userId));
    }

    public void addFollower(int userId) {
        if (!this.followers.contains(userId)) {
            this.followers.add(userId);
        }
    }

    public void removeFollower(int userId) {
        this.followers.remove(Integer.valueOf(userId));
    }

    public void addPostId(int postId) {
        this.postIds.add(postId);
    }

    public void removePostId(int postId) {
        this.postIds.remove(Integer.valueOf(postId));
    }

    public void addSelectedHashtag(int hashtagId) {
        if (!this.selectedHashtagIds.contains(hashtagId) &&
                this.selectedHashtagIds.size() < 4) {
            this.selectedHashtagIds.add(hashtagId);
        }
    }

    public void clearSelectedHashtags() {
        this.selectedHashtagIds.clear();
    }

    public void increaseReportCount() {
        this.reportCount++;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getTokens() {
        return tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    public Date getCreatedAt() {
        return createdDate;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public int getReportCount() {
        return reportCount;
    }

    public List<Integer> getFollowers() {
        return new ArrayList<>(followers);
    }

    public List<Integer> getFollowing() {
        return new ArrayList<>(followings);
    }

    public List<Integer> getPostIds() {
        return new ArrayList<>(postIds);
    }

    public List<Integer> getSelectedHashtagIds() {
        return new ArrayList<>(selectedHashtagIds);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }
    public String getProfileCover() { return profileCover; }
    public void setProfileCover(String profileCover) { this.profileCover = profileCover; }

    public void setId(int id) {
        this.id = id;
    }
}
