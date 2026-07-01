package com.example.demo1.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

public class Post implements Iterable<String>, Comparable<Post>{
    private static int nextId = 1;

    private int id;
    private String text;
    private int authorId;
    private Media media;
    private List<Integer> hashtags;
    private List<Integer> likeUserIds;
    private List<Integer> replyIds;
    private Integer parentPostId;

    private Integer repostOfId;
    private List<Integer> repostUserIds;

    private int views;
    private boolean isLocked;
    private boolean isDeleted;
    private Date createdDate;
    private int reportCount;

    public Post(String text, int authorId, Media media, Integer parentPostId) {
        this.id = nextId++;
        this.text = text;
        this.authorId = authorId;
        this.media = media;
        this.parentPostId = parentPostId;
        this.hashtags = new ArrayList<>();
        this.likeUserIds = new ArrayList<>();
        this.replyIds = new ArrayList<>();


        this.repostOfId = null;
        this.repostUserIds = new ArrayList<>();

        this.views = 0;
        this.isLocked = false;
        this.isDeleted = false;
        this.createdDate = new Date();
        this.reportCount = 0;
    }

    public Integer getRepostOfId() {
        return repostOfId;
    }

    public void setRepostOfId(Integer repostOfId) {
        this.repostOfId = repostOfId;
    }

    public boolean isRepost() {
        return repostOfId != null;
    }

    public List<Integer> getRepostUserIds() {
        return new ArrayList<>(repostUserIds);
    }

    public void addRepostUser(int userId) {
        if (!repostUserIds.contains(userId)) {
            repostUserIds.add(userId);
        }
    }

    public void addLike(int userId) {
        if (!likeUserIds.contains(userId)) {
            likeUserIds.add(userId);
        }
    }

    public void removeLike(int userId) {
        likeUserIds.remove(Integer.valueOf(userId));
    }

    public void addView() {
        this.views++;
    }

    public void addReply(int replyId) {
        if (!replyIds.contains(replyId)) {
            replyIds.add(replyId);
        }
    }
    public void removeReply(int replyId) {
        replyIds.remove(Integer.valueOf(replyId));
    }
    public void addHashtag(int hashtagId) {
        if (!hashtags.contains(hashtagId)) {
            hashtags.add(hashtagId);
        }
    }
    public void removeHashtag(int hashtagId) {
        hashtags.remove(Integer.valueOf(hashtagId));
    }

    public void increaseReportCount() {
        this.reportCount++;
    }
    public void decreaseReportCount() {
        if (reportCount > 0) {
            reportCount--;
        }
    }

    public boolean isLikedByUser(int userId) {
        return likeUserIds.contains(userId);
    }

    public int getLikeCount() {
        return likeUserIds.size();
    }

    public int getReplyCount() {
        return replyIds.size();
    }

    public boolean isReply() {
        return parentPostId != null;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getAuthorId() {
        return authorId;
    }

    public Media getMedia() {
        return media;
    }
    public void setMedia(Media media) {
        this.media = media;
    }

    public List<Integer> getHashtags() {
        return new ArrayList<>(hashtags);
    }

    public List<Integer> getLikeUserIds() {
        return new ArrayList<>(likeUserIds);
    }

    public List<Integer> getReplyIds() {
        return new ArrayList<>(replyIds);
    }

    public Integer getParentPostId() {
        return parentPostId;
    }

    public int getViews() {
        return views;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public int getReportCount() {
        return reportCount;
    }

    @Override
    public int compareTo(Post o) {
        if (this.text.equals(o.getText()) && this.hashtags.equals(o.getHashtags())) {
            return 0;
        }
        return 1;
    }

    @Override
    public Iterator<String> iterator() {
        if (this.text == null || this.text.trim().isEmpty()) {
            return Collections.emptyIterator();
        }
        return Arrays.asList(this.text.split("\\s+")).iterator();
    }
}