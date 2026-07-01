package com.example.demo1.View;

import com.example.demo1.Model.User;
import com.example.demo1.XApplication;
import javafx.event.ActionEvent;
import com.example.demo1.Controller.PostController;
import com.example.demo1.Controller.UserController;
import com.example.demo1.Model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.util.List;
import javafx.scene.paint.Color;

public class ProfileViewController {

    @FXML private Label lblUsername;
    @FXML private Label lblFullName;
    @FXML private Label lblBio;
    @FXML private Label lblFollowers;
    @FXML private Label lblFollowing;
    @FXML private Label lblTokens;
    @FXML private Label lblPostsCount;
    @FXML private VBox postsContainer;
    @FXML private ScrollPane scrollPane;

    @FXML private TextArea txtBio;
    @FXML private Button btnEditSave;
    @FXML private Button btnFollow;

    private User currentUser;
    private User viewedUser;
    private UserController userController = new UserController();
    private PostController postController = new PostController();
    private Database db = Database.getInstance();
    private boolean isEditing = false;

    @FXML
    private void initialize() {
        if (txtBio != null) txtBio.setVisible(false);
        if (btnFollow != null) btnFollow.setVisible(false);
    }

    public void setCurrentUser(User currentUser){
        this.currentUser = currentUser;
    }

    public void setViewedUser(User viewedUser) {
        this.viewedUser = viewedUser;
        loadProfile();
    }

    private void loadProfile() {
        if (viewedUser == null) return;
        if (viewedUser.isLocked()) {
            lblBio.setText("⚠️ This account has been locked by Admin.");
            lblBio.setTextFill(Color.RED); // نیاز به import رنگ
            btnFollow.setDisable(true); // غیرفعال کردن دکمه فالو برای کاربر قفل شده
        }

        lblUsername.setText("@" + viewedUser.getUsername() + " " + viewedUser.getSubscriptionBadge());
        lblFullName.setText(viewedUser.getFullName());
        lblBio.setText(viewedUser.getBio().isEmpty() ? "No bio yet" : viewedUser.getBio());
        lblFollowers.setText("Followers: " + viewedUser.getFollowers().size());
        lblFollowing.setText("Following: " + viewedUser.getFollowing().size());

        if (lblTokens != null) lblTokens.setText("Tokens: " + viewedUser.getTokens());

        List<Post> userPosts = userController.getUserPosts(viewedUser.getId());
        if (lblPostsCount != null) lblPostsCount.setText("Posts: " + userPosts.size());

        loadPosts(userPosts);

        if (currentUser.getId() == viewedUser.getId()) {
            if (btnEditSave != null) btnEditSave.setVisible(true);
            if (btnFollow != null) btnFollow.setVisible(false);
        } else {
            if (btnEditSave != null) btnEditSave.setVisible(false);
            if (btnFollow != null) {
                btnFollow.setVisible(true);
                updateFollowButtonText();
            }
        }
    }

    private void updateFollowButtonText() {
        boolean isFollowing = currentUser.getFollowing().contains(viewedUser.getId());
        btnFollow.setText(isFollowing ? "Unfollow" : "Follow");
    }

    @FXML
    public void handleFollow(ActionEvent event) {
        if (currentUser == null || viewedUser == null) return;

        boolean isFollowing = currentUser.getFollowing().contains(viewedUser.getId());

        if (isFollowing) {
            userController.unfollowUser(currentUser.getId(), viewedUser.getId());
        } else {
            userController.followUser(currentUser.getId(), viewedUser.getId());
        }

        currentUser = db.getUserById(currentUser.getId());

        updateFollowButtonText();

        User updatedViewedUser = db.getUserById(viewedUser.getId());
        if (updatedViewedUser != null) {
            lblFollowers.setText("Followers: " + updatedViewedUser.getFollowers().size());
        }
    }

    private void loadPosts(List<Post> posts) {
        if (postsContainer == null) return;

        postsContainer.getChildren().clear();

        if (posts.isEmpty()) {
            postsContainer.getChildren().add(new Label("No posts yet!"));
            return;
        }

        for (Post post : posts) {
            VBox card = new VBox(5);
            Label text = new Label(post.getText());
            text.setWrapText(true);
            Label stats = new Label("Likes: " + post.getLikeCount() + " | Replies: " + post.getReplyCount() + " | Views: " + post.getViews());
            card.getChildren().addAll(text, stats);
            postsContainer.getChildren().add(card);
        }
    }

    @FXML
    public void handleEditSave(ActionEvent event) {
        if (!isEditing) {
            isEditing = true;
            txtBio.setText(viewedUser.getBio());
            txtBio.setVisible(true);
            lblBio.setVisible(false);
            btnEditSave.setText("Save");
        } else {
            String newBio = txtBio.getText().trim();
            userController.editProfile(viewedUser.getId(), newBio, null, null, null, null);

            viewedUser = db.getUserById(viewedUser.getId());
            lblBio.setText(viewedUser.getBio().isEmpty() ? "No bio yet" : viewedUser.getBio());
            lblBio.setVisible(true);
            txtBio.setVisible(false);
            isEditing = false;
            btnEditSave.setText("Edit Profile");

            showMessage("Success", "Profile updated!");
        }
    }

    @FXML
    public void handleBuyPremium(ActionEvent event) {
        if (viewedUser.getId() != currentUser.getId()) {
            showMessage("Error", "You can only buy premium for your own profile!");
            return;
        }

        // باز کردن یک دیالوگ انتخاب برای نوع اشتراک
        List<String> options = List.of("Blue Tier (200 Tokens)", "Gold Tier (500 Tokens)");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Blue Tier (200 Tokens)", options);
        dialog.setTitle("Upgrade Account");
        dialog.setHeaderText("Choose your X Premium Subscription:");
        dialog.setContentText("Subscription:");

        dialog.showAndWait().ifPresent(selection -> {
            String tier = selection.startsWith("Blue") ? "Blue" : "Gold";
            int cost = tier.equals("Blue") ? 200 : 500;

            if (currentUser.getTokens() >= cost) {
                // کسر توکن و تغییر نوع اشتراک
                currentUser.setTokens(currentUser.getTokens() - cost);

                // بسته به منطق مدل خود، متد مربوطه را صدا بزنید (مثلاً تغییر بج یا فیلد نوع حساب)
                // currentUser.setSubscriptionBadge(tier.equals("Blue") ? "💙" : "👑");

                db.updateUser(currentUser); // ذخیره در دیتابیس
                loadProfile(); // بازنشانی صفحه
                showMessage("Success", "Welcome to X " + tier + "! Your profile upgraded successfully.");
            } else {
                showMessage("Error", "Inssuficient tokens! You need " + cost + " tokens.");
            }
        });
    }

    @FXML
    public void handleBack(ActionEvent event) {
        XApplication.showTimelineView(currentUser);
    }

    private void showMessage(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
