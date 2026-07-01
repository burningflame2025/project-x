package com.example.demo1.Controller;

import com.example.demo1.Model.Database;
import com.example.demo1.Model.Hashtag;
import com.example.demo1.Model.Post;
import com.example.demo1.Model.User;
import com.example.demo1.Model.Media;
import com.example.demo1.Model.SubscriptionType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PostController {
    private Database db = Database.getInstance();

    public String createPost(int userId, String text, Media media, List<String> hashtagTitles, Integer parentPostId) {
        if (text == null || text.trim().isEmpty())
            return "write something!";

        User user = db.getUserById(userId);
        if (user == null) return "user not found";
        if (user.isLocked()) return "user is locked";

        if (parentPostId != null) {
            Post parent = db.getPostById(parentPostId);
            if (parent == null) return "parent post not found";
            if (parent.isDeleted()) return "parent post deleted";
            if (parent.isLocked()) return "parent post locked";
        }

        int cost = user.calculatePostCost(text, media != null);
        if (!user.reduceTokens(cost)) {
            return "not enough tokens! need " + cost + ", have " + user.getTokens();
        }

        Post post = new Post(text, userId, media, parentPostId);
        db.addPost(post);
        user.addPostId(post.getId());

        if (hashtagTitles != null) {
            for (String title : hashtagTitles) {
                if (!title.trim().isEmpty()) {
                    Hashtag hashtag = db.getOrCreateHashtag(title.trim());
                    hashtag.addPost(post.getId());
                    post.addHashtag(hashtag.getId());
                }
            }
        }

        if (parentPostId != null) {
            Post parent = db.getPostById(parentPostId);
            if (parent != null) {
                parent.addReply(post.getId());
            }
        }
        db.updateUser(user);
        return "Post created successfully!";
    }

    public String likePost(int userId, int postId) {
        User user = db.getUserById(userId);
        Post post = db.getPostById(postId);
        if (user == null || post == null) return "not found";
        if (post.isDeleted()) return "post deleted";
        if (post.isLocked()) return "post locked";
        if (post.isLikedByUser(userId)) return "already liked";

        post.addLike(userId);
        return "post liked";
    }

    public String unlikePost(int userId, int postId) {
        Post post = db.getPostById(postId);
        if (post == null) return "post not found";
        if (post.isDeleted()) return "post deleted";
        if (post.isLocked()) return "post locked";
        if (!post.isLikedByUser(userId)) return "not liked yet";

        post.removeLike(userId);
        return "post unliked";
    }

    public boolean isPostLikedByUser(int userId, int postId) {
        Post post = db.getPostById(postId);
        return post != null && post.isLikedByUser(userId);
    }

    public String editPost(int userId, int postId, String newText) {
        if (newText == null || newText.trim().isEmpty()) return "Text cannot be empty!";

        Post post = db.getPostById(postId);
        if (post == null) return "Post not found";
        if (post.isDeleted()) return "Post is deleted";
        if (post.isLocked()) return "Post is locked";
        if (post.getAuthorId() != userId) return "You can only edit your own posts";

        User user = db.getUserById(userId);
        if (user == null) return "User not found";
        if (user.getSubscriptionType() == SubscriptionType.NONE) {
            return "Only Premium (Blue/Gold) users can edit their posts!";
        }

        post.setText(newText);
        return "Post updated successfully!";
    }

    public String replyToPost(int userId, int parentPostId, String text, Media media, List<String> hashtags) {
        return createPost(userId, text, media, hashtags, parentPostId);
    }

    public Post viewPost(int postId) {
        Post post = db.getPostById(postId);
        if (post != null && !post.isDeleted() && !post.isLocked()) {
            post.addView();
        }
        return post;
    }

    public String deletePost(int userId, int postId) {
        Post post = db.getPostById(postId);
        if (post == null) return "post not found";
        if (post.getAuthorId() != userId) return "you can only delete your own posts";

        post.setDeleted(true);
        User user = db.getUserById(userId);
        if (user != null) {
            user.removePostId(postId);
        }
        return "post deleted";
    }

    public List<Post> getReplies(int postId) {
        Post post = db.getPostById(postId);
        List<Post> replies = new ArrayList<>();
        if (post == null) return replies;

        for (int id : post.getReplyIds()) {
            Post reply = db.getPostById(id);
            if (reply != null && !reply.isDeleted() && !reply.isLocked()) {
                replies.add(reply);
            }
        }
        replies.sort(Comparator.comparing(Post::getCreatedDate));
        return replies;
    }

    // این خط را جایگزین خط قبلی کنید:
    public List<Post> getHomeTimeline(int userId) {
        User user = db.getUserById(userId);
        if (user == null) return new ArrayList<>();

        List<Post> timeline = new ArrayList<>();
        timeline.addAll(db.getPostsByUserId(userId));

        for (int followingId : user.getFollowing()) {
            timeline.addAll(db.getPostsByUserId(followingId));
        }

        for (int hashtagId : user.getSelectedHashtagIds()) {
            timeline.addAll(db.getPostsByHashtagId(hashtagId));
        }

        List<Post> distinctTimeline = new ArrayList<>();
        for (Post post : timeline) {
            if (!distinctTimeline.contains(post) && !post.isDeleted() && !post.isLocked()) {
                distinctTimeline.add(post);
            }
        }

        // مرتب‌سازی پیش‌فرض بر اساس تاریخ
        distinctTimeline.sort((o1, o2) -> o2.getCreatedDate().compareTo(o1.getCreatedDate()));

        int limit = 10;
        int toIndex = Math.min(limit, distinctTimeline.size());
        return new ArrayList<>(distinctTimeline.subList(0, toIndex));
    }
    public List<Post> getPopularPosts(int limit) {
        return db.getPopularPosts(limit);
    }

    public String getPostDetails(int postId) {
        Post post = db.getPostById(postId);
        if (post == null) return "post not found";
        if (post.isDeleted()) return "post is deleted";
        if (post.isLocked()) return "post is locked by admin";

        User author = db.getUserById(post.getAuthorId());
        if (author == null) return "author not found";

        return String.format("📝 Post #%d by @%s %s\n   Text: %s\n   ❤️ %d | 💬 %d | 👁️ %d\n   📅 %s",
                post.getId(), author.getUsername(), author.getSubscriptionBadge(),
                post.getText().length() > 50 ? post.getText().substring(0, 50) + "..." : post.getText(),
                post.getLikeCount(), post.getReplyCount(), post.getViews(), post.getCreatedDate()
        );
    }
    public String sharePost(int postId) {
        Post post = db.getPostById(postId);
        if (post == null || post.isDeleted() || post.isLocked()) {
            return "Error: Post is unavailable!";
        }
        return "https://x.com/Posts/" + postId;
    }

    public List<Post> getRecommendedPosts(int userId) {
        User user = db.getUserById(userId);
        if (user == null) return new ArrayList<>();

        List<Post> recommended = new ArrayList<>();

        // پیدا کردن پست‌هایی که فالوینگ‌های این کاربر لایک کرده‌اند
        for (int followingId : user.getFollowing()) {
            // جستجو در تمام پست‌ها برای پیدا کردن لایک‌های فالوینگ‌ها
            for (Post p : db.getAllPosts()) {
                if (p.isLikedByUser(followingId) && !recommended.contains(p) && p.getAuthorId() != userId) {
                    recommended.add(p);
                }
            }
        }

        // محدود کردن به حداکثر ۱۰ پست
        int limit = 10;
        int toIndex = Math.min(limit, recommended.size());
        return new ArrayList<>(recommended.subList(0, toIndex));
    }
    public String repostPost(int userId, int originalPostId) {
        User user = db.getUserById(userId);
        Post originalPost = db.getPostById(originalPostId);

        if (user == null || originalPost == null) return "User or Post not found";
        if (user.isLocked()) return "Your account is locked";
        if (originalPost.isDeleted()) return "Cannot repost a deleted post";
        if (originalPost.isLocked()) return "Cannot repost a locked post";

        if (originalPost.getRepostUserIds() != null && originalPost.getRepostUserIds().contains(userId)) {
            return "You have already reposted this post!";
        }

        int cost = user.calculatePostCost("", false);
        if (!user.reduceTokens(cost)) {
            return "Not enough tokens! need " + cost + ", have " + user.getTokens();
        }

        Post repost = new Post("", userId, null, null);
        repost.setRepostOfId(originalPostId);

        db.addPost(repost);
        user.addPostId(repost.getId());
        originalPost.addRepostUser(userId);
        db.updateUser(user);

        return "Post reposted successfully!";
    }
}