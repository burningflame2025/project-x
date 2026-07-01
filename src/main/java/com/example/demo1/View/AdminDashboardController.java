package com.example.demo1.View;

import com.example.demo1.Controller.AdminController;
import com.example.demo1.Controller.ReportController;
import com.example.demo1.Model.*;
import com.example.demo1.XApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class AdminDashboardController {

    @FXML private ListView<String> listReports;
    @FXML private TextArea txtReportDetails;

    private final AdminController adminController = new AdminController();
    private final ReportController reportController = new ReportController();
    private final Database db = Database.getInstance();
    private List<Report> currentReports;

    @FXML
    private void initialize() {
        loadWaitingReports();
    }

    private void loadWaitingReports() {
        currentReports = reportController.getWaitingReports();
        listReports.getItems().clear();

        if (currentReports.isEmpty()) {
            listReports.getItems().add("📭 No pending reports");
            return;
        }

        for (Report report : currentReports) {
            User reporter = db.getUserById(report.getReporterId());
            User reported = db.getUserById(report.getReportedUserId());
            String item = "📋 Report #" + report.getId() + " | " + report.getReportType() +
                    " | From: @" + (reporter != null ? reporter.getUsername() : "?") +
                    " | About: @" + (reported != null ? reported.getUsername() : "?");
            listReports.getItems().add(item);
        }

        listReports.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null && !selected.equals("📭 No pending reports")) {
                showReportDetails(selected);
            }
        });
    }

    private void showReportDetails(String selectedItem) {
        try {
            String[] parts = selectedItem.split("\\|");
            String firstPart = parts[0].replace("📋 Report #", "").trim();
            int reportId = Integer.parseInt(firstPart);

            Report report = db.getReportById(reportId);
            if (report != null) {
                User reporter = db.getUserById(report.getReporterId());
                User reported = db.getUserById(report.getReportedUserId());

                String details = "═══════════════════════════════════\n" +
                        "📋 REPORT #" + report.getId() + "\n" +
                        "═══════════════════════════════════\n" +
                        "📌 Type: " + report.getReportType() + "\n" +
                        "👤 Reporter: @" + (reporter != null ? reporter.getUsername() : "?") + "\n" +
                        "👤 Reported: @" + (reported != null ? reported.getUsername() : "?") + "\n" +
                        "🆔 Content ID: " + report.getContentId() + "\n" +
                        "📅 Date: " + report.getCreatedDate() + "\n" +
                        "📝 Status: " + report.getStatus() + "\n" +
                        "───────────────────────────────────\n" +
                        "💬 Description:\n" + report.getDescription() + "\n" +
                        "═══════════════════════════════════";

                txtReportDetails.setText(details);
            }
        } catch (Exception e) {
            txtReportDetails.setText("Error loading report details!");
        }
    }

    @FXML
    public void handleConfirmReport(ActionEvent event) {
        String selected = listReports.getSelectionModel().getSelectedItem();
        if (selected == null || selected.equals("📭 No pending reports")) {
            showMessage("Error", "Please select a report first!");
            return;
        }

        try {
            String[] parts = selected.split("\\|");
            int reportId = Integer.parseInt(parts[0].replace("📋 Report #", "").trim());

            String result = adminController.confirmReport(reportId);
            showMessage("Result", result);
            loadWaitingReports();
            txtReportDetails.clear();
        } catch (Exception e) {
            showMessage("Error", "Invalid report ID!");
        }
    }

    @FXML
    public void handleRejectReport(ActionEvent event) {
        String selected = listReports.getSelectionModel().getSelectedItem();
        if (selected == null || selected.equals("📭 No pending reports")) {
            showMessage("Error", "Please select a report first!");
            return;
        }

        try {
            String[] parts = selected.split("\\|");
            int reportId = Integer.parseInt(parts[0].replace("📋 Report #", "").trim());

            String result = adminController.rejectReport(reportId);
            showMessage("Result", result);
            loadWaitingReports();
            txtReportDetails.clear();
        } catch (Exception e) {
            showMessage("Error", "Invalid report ID!");
        }
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        loadWaitingReports();
        showMessage("Refresh", "Reports list refreshed!");
    }

    @FXML
    public void handleViewAllUsers(ActionEvent event) {
        List<User> users = adminController.getAllUsers();

        if (users.isEmpty()) {
            showMessage("Users", "No users found!");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════\n");
        sb.append("👥 ALL USERS (").append(users.size()).append(")\n");
        sb.append("═══════════════════════════════════\n\n");

        for (User u : users) {
            sb.append("🆔 ID: ").append(u.getId()).append("\n");
            sb.append("   @").append(u.getUsername()).append(" ").append(u.getSubscriptionBadge()).append("\n");
            sb.append("   📛 ").append(u.getFullName()).append("\n");
            sb.append("   👥 Followers: ").append(u.getFollowers().size()).append("\n");
            sb.append("   🔒 Locked: ").append(u.isLocked() ? "Yes" : "No").append("\n");
            sb.append("   💰 Tokens: ").append(u.getTokens()).append("\n");
            sb.append("───────────────────────────────────\n");
        }

        showMessage("All Users", sb.toString());
    }

    @FXML
    public void handleViewAllPosts(ActionEvent event) {
        List<Post> posts = adminController.getAllPosts();

        if (posts.isEmpty()) {
            showMessage("Posts", "No posts found!");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════\n");
        sb.append("📝 ALL POSTS (").append(posts.size()).append(")\n");
        sb.append("═══════════════════════════════════\n\n");

        for (Post p : posts) {
            User author = db.getUserById(p.getAuthorId());
            sb.append("🆔 Post #").append(p.getId()).append("\n");
            sb.append("   👤 Author: @").append(author != null ? author.getUsername() : "?").append("\n");
            sb.append("   📝 Text: ").append(p.getText().length() > 50 ? p.getText().substring(0, 50) + "..." : p.getText()).append("\n");
            sb.append("   ❤️ Likes: ").append(p.getLikeCount()).append("\n");
            sb.append("   💬 Replies: ").append(p.getReplyCount()).append("\n");
            sb.append("   👁️ Views: ").append(p.getViews()).append("\n");
            sb.append("   🔒 Locked: ").append(p.isLocked() ? "Yes" : "No").append("\n");
            sb.append("───────────────────────────────────\n");
        }

        showMessage("All Posts", sb.toString());
    }

    @FXML
    public void handleLockUser(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Lock User");
        dialog.setHeaderText("Enter User ID to lock");
        dialog.setContentText("User ID:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                int userId = Integer.parseInt(input.trim());
                String result = adminController.lockUser(userId);
                showMessage("Result", result);
            } catch (NumberFormatException e) {
                showMessage("Error", "Invalid User ID!");
            }
        });
    }

    @FXML
    public void handleUnlockUser(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Unlock User");
        dialog.setHeaderText("Enter User ID to unlock");
        dialog.setContentText("User ID:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                int userId = Integer.parseInt(input.trim());
                String result = adminController.unlockUser(userId);
                showMessage("Result", result);
            } catch (NumberFormatException e) {
                showMessage("Error", "Invalid User ID!");
            }
        });
    }

    @FXML
    public void handleLockPost(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Lock Post");
        dialog.setHeaderText("Enter Post ID to lock");
        dialog.setContentText("Post ID:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                int postId = Integer.parseInt(input.trim());
                String result = adminController.lockPost(postId);
                showMessage("Result", result);
            } catch (NumberFormatException e) {
                showMessage("Error", "Invalid Post ID!");
            }
        });
    }

    @FXML
    public void handleUnlockPost(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Unlock Post");
        dialog.setHeaderText("Enter Post ID to unlock");
        dialog.setContentText("Post ID:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                int postId = Integer.parseInt(input.trim());
                String result = adminController.unlockPost(postId);
                showMessage("Result", result);
            } catch (NumberFormatException e) {
                showMessage("Error", "Invalid Post ID!");
            }
        });
    }

    @FXML
    public void handlePopularStats(ActionEvent event) {
        List<Post> popularPosts = adminController.getPopularPosts(5);
        List<Hashtag> popularHashtags = adminController.getPopularHashtags();

        StringBuilder result = new StringBuilder();
        result.append("═══════════════════════════════════\n");
        result.append("📊 POPULAR STATISTICS\n");
        result.append("═══════════════════════════════════\n\n");

        result.append("🏆 TOP 5 POSTS (by likes):\n");
        result.append("───────────────────────────────────\n");

        if (popularPosts.isEmpty()) {
            result.append("   No posts yet!\n");
        } else {
            for (int i = 0; i < popularPosts.size(); i++) {
                Post p = popularPosts.get(i);
                User author = db.getUserById(p.getAuthorId());
                result.append((i+1) + ". @" + (author != null ? author.getUsername() : "?") + "\n");
                result.append("   ❤️ " + p.getLikeCount() + " likes\n");
                result.append("   👁️ " + p.getViews() + " views\n");
                result.append("   💬 " + p.getReplyCount() + " replies\n");
                if (i < popularPosts.size() - 1) result.append("\n");
            }
        }

        result.append("\n───────────────────────────────────\n");
        result.append("🏷️ POPULAR HASHTAGS:\n");
        result.append("───────────────────────────────────\n");

        if (popularHashtags.isEmpty()) {
            result.append("   No hashtags yet!\n");
        } else {
            for (int i = 0; i < Math.min(10, popularHashtags.size()); i++) {
                Hashtag h = popularHashtags.get(i);
                result.append((i+1) + ". " + h.getTitle() + "\n");
                result.append("   Used " + h.getUsageCount() + " times\n");
                if (i < Math.min(10, popularHashtags.size()) - 1) result.append("\n");
            }
        }

        result.append("\n═══════════════════════════════════");

        showMessage("Popular Statistics", result.toString());
    }

    @FXML
    public void handleBack(ActionEvent event) {
        XApplication.showLoginView();
    }

    private void showMessage(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
    }
}