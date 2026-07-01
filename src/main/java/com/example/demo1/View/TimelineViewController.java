package com.example.demo1.View;

import com.example.demo1.Controller.PostController;
import com.example.demo1.Controller.ReportController;
import com.example.demo1.Controller.SearchController;
import com.example.demo1.Controller.UserController;
import com.example.demo1.Model.*;
import com.example.demo1.XApplication;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.*;

import static com.example.demo1.Model.ReportType.POST;

public class TimelineViewController {

    @FXML private Label lblUserInfo;
    @FXML private Label lblTokens;
    @FXML private VBox timelineContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private TextArea txtNewPost;
    @FXML private TextField txtNewHashtags;
    @FXML private ChoiceBox<String> choiceSortBy;

    private User currentUser;
    private PostController postController = new PostController();
    private UserController userController = new UserController();
    private SearchController searchController = new SearchController();
    private Database db = Database.getInstance();
    private ObservableList<Post> currentPosts = FXCollections.observableArrayList();
    private Timeline tokenUpdater;

    @FXML
    private void initialize() {
        choiceSortBy.getItems().addAll("Newest First", "Most Liked", "Most Viewed");
        choiceSortBy.setValue("Newest First");
        choiceSortBy.setOnAction(e -> refreshTimeline());
        scrollPane.setFitToWidth(true);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUserInfo();
        refreshTimeline();
        startTokenUpdateTimer();
    }

    private void startTokenUpdateTimer() {
        if (tokenUpdater != null) tokenUpdater.stop();
        tokenUpdater = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            User updatedUser = db.getUserById(currentUser.getId());
            if (updatedUser != null) {
                currentUser = updatedUser;
                lblTokens.setText(currentUser.getTokens() + " tokens");
            }
        }));
        tokenUpdater.setCycleCount(Animation.INDEFINITE);
        tokenUpdater.play();
    }

    private void updateUserInfo() {
        if (currentUser != null){
            lblUserInfo.setText(currentUser.getUsername() + " " + currentUser.getSubscriptionBadge());
            lblTokens.setText(currentUser.getTokens() + " tokens");
        }
    }

    private void refreshTimeline() {
        timelineContainer.getChildren().clear();

        // ۱. دریافت پست‌های فالووینگ‌ها
        List<Post> timeline = postController.getHomeTimeline(currentUser.getId());
        // ۲. دریافت پست‌های خود کاربر (جدید)
        List<Post> ownPosts = userController.getUserPosts(currentUser.getId());

        // ترکیب و حذف پست‌های تکراری یا قفل شده
        Set<Integer> addedPostIds = new HashSet<>();
        List<Post> visiblePosts = new ArrayList<>();

        for (Post p : timeline) {
            if (!p.isLocked() && addedPostIds.add(p.getId())) {
                visiblePosts.add(p);
            }
        }
        for (Post p : ownPosts) {
            if (!p.isLocked() && addedPostIds.add(p.getId())) {
                visiblePosts.add(p);
            }
        }

        currentPosts.setAll(visiblePosts);
        sortPosts(); // مرتب‌سازی بر اساس فیلتر انتخابی (جدیدترین، محبوب‌ترین و...)

        if (currentPosts.isEmpty()) {
            timelineContainer.getChildren().add(new Label("No posts to show!"));
        } else {
            for (Post post : currentPosts) {
                timelineContainer.getChildren().add(createPostCard(post));
            }
        }
    }

    private VBox createPostCard(Post post) {
        User author = db.getUserById(post.getAuthorId());
        if (author == null) return new VBox();

        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        Label headerLabel = new Label(author.getUsername() + " (" + author.getSubscriptionBadge() + ") - " + getTimeAgo(post.getCreatedDate()));

        Label postText = new Label(post.getText());
        postText.setWrapText(true);

        Label statsLabel = new Label("Likes: " + post.getLikeCount() + " | Replies: " + post.getReplyCount() + " | Views: " + post.getViews());

        HBox actionBox = new HBox(10);
        Button likeBtn = new Button(post.isLikedByUser(currentUser.getId()) ? "Unlike" : "Like");
        Button replyBtn = new Button("Reply");
        Button reportBtn = new Button("Report");

        likeBtn.setOnAction(e -> handleLike(post, likeBtn, statsLabel));
        replyBtn.setOnAction(e -> showReplyDialog(post));
        reportBtn.setOnAction(e -> showReportDialog(post, author));

        actionBox.getChildren().addAll(likeBtn, replyBtn, reportBtn);
        card.getChildren().addAll(headerLabel, postText, statsLabel, actionBox);

        return card;
    }

    private String getTimeAgo(Date date) {
        long diff = System.currentTimeMillis() - date.getTime();
        long minutes = diff / (60 * 1000);
        long hours = minutes / 60;
        long days = hours / 24;
        if (days > 0) return days + " days ago";
        if (hours > 0) return hours + " hours ago";
        if (minutes > 0) return minutes + " minutes ago";
        return "just now";
    }

    private void showReportDialog(Post post, User author) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Report Post");
        dialog.setHeaderText("Report post by " + author.getUsername());
        dialog.setContentText("Reason: ");

        dialog.showAndWait().ifPresent(reason -> {
            if (! reason.trim().isEmpty()) {
                ReportController reportController = new ReportController();
                reportController.createReport(currentUser.getId(), post.getId(), author.getId(), reason, POST);
                showMessage("Report submitted", "Thank you for your report!");
            }
        });
    }
    private void showMessage(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showReplyDialog(Post post) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reply");
        dialog.setHeaderText("Write your reply:");
        dialog.setContentText("Reply:");

        dialog.showAndWait().ifPresent(replyText -> {
            if (!replyText.trim().isEmpty()) {
                String result = postController.replyToPost(currentUser.getId(),
                        post.getId(), replyText, null, null);
                if (result.startsWith("replied")) {
                    refreshTimeline();
                    showMessage("Success", "Reply posted!");
                } else {
                    showMessage("Error", result);
                }
            }
        });
    }

    private void handleLike(Post post, Button likeBtn, Label statsLabel) {
        if (post.isLikedByUser(currentUser.getId())) {
            postController.unlikePost(currentUser.getId(), post.getId());
            likeBtn.setText("Like");
        } else {
            postController.likePost(currentUser.getId(), post.getId());
            likeBtn.setText("Unlike");
        }
        statsLabel.setText("Likes: " + post.getLikeCount() + " | Replies: " + post.getReplyCount() + " | Views: " + post.getViews());
    }

    private void sortPosts() {
        switch (choiceSortBy.getValue()) {
            case "Most Liked" -> currentPosts.sort((a, b) -> Integer.compare(b.getLikeCount(), a.getLikeCount()));
            case "Most Viewed" -> currentPosts.sort((a, b) -> Integer.compare(b.getViews(), a.getViews()));
            default -> currentPosts.sort((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()));
        }
    }

    public void handleViewProfile(ActionEvent event) {
        XApplication.showProfileView(currentUser, currentUser);
    }

    public void handleLogout(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to logout?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                com.example.demo1.XApplication.showLoginView();
            }
        });
    }

    public void handleRefresh(ActionEvent event) {
        refreshTimeline();
    }

    public void handleSearch(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search");
        dialog.setHeaderText("Search by username:");
        dialog.setContentText("Username:");

        dialog.showAndWait().ifPresent(keyword -> {
            if (!keyword.trim().isEmpty()) {
                List<User> users = searchController.searchByUsername(keyword);
                String result = "Users found: " + users.size() + "\n";
                for (User u : users) {
                    result += "  - " + u.getUsername() + "\n";
                }
                showMessage("Search Results", result);
            }
        });
    }
    public void handleCreatePost() {
        String text = txtNewPost.getText().trim();
        if (text.isEmpty()) {
            showMessage("Error", "Post cannot be empty!");
            return;
        }

        List<String> hashtags = new ArrayList<>();
        String hashtagsInput = txtNewHashtags.getText().trim();
        if (!hashtagsInput.isEmpty()) {
            for (String h : hashtagsInput.split(",")) {
                hashtags.add(h.trim());
            }
        }

        String result = postController.createPost(currentUser.getId(), text, null, hashtags, null);

        if (result.startsWith("Post created") || result.startsWith("Reply created")) {
            txtNewPost.clear();
            txtNewHashtags.clear();
            refreshTimeline();
            updateUserInfo();
            showMessage("Success", result);
        } else {
            showMessage("Error", result);
        }
    }
}
