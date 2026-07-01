package com.example.demo1;

import com.example.demo1.Model.Database;
import com.example.demo1.Model.User;
import com.example.demo1.View.ProfileViewController;
import com.example.demo1.View.TimelineViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class XApplication extends Application {

    private static Stage primaryStage;
    private static Database db = Database.getInstance();

    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 700;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        stage.setMinWidth(WINDOW_WIDTH);
        stage.setMinHeight(WINDOW_HEIGHT);

        showLoginView();

        stage.setTitle("X Simulator - Twitter Clone");
        stage.show();
    }

    private static FXMLLoader switchScene(String fxmlPath, String title) {
        try {

            System.out.println("Loading: " + fxmlPath);

            var resource = XApplication.class.getResource(fxmlPath);

            System.out.println(resource);

            if (resource == null) {
                throw new RuntimeException("FXML not found: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(resource);

            Parent root = loader.load();

            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

            primaryStage.setScene(scene);
            primaryStage.setTitle(title);

            return loader;

        } catch (Exception e) {
            System.err.println("Error loading: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }

    public static void showLoginView() {
        switchScene(
                "/com/example/demo1/login-view.fxml",
                "Login - Twitter"
        );
    }

    public static void showRegisterView() {
        switchScene(
                "/com/example/demo1/register-view.fxml",
                "Register - Twitter"
        );
    }

    public static void showAdminDashboard() {
        switchScene(
                "/com/example/demo1/admin-dashboard.fxml",
                "Admin Dashboard"
        );
    }

    public static void showTimelineView(User user) {

        FXMLLoader loader = switchScene(
                "/com/example/demo1/timeline-view.fxml",
                "Home - Twitter"
        );

        if (loader != null) {
            TimelineViewController controller =
                    loader.getController();

            controller.setCurrentUser(user);
        }
    }

    public static void showProfileView(User currentUser, User viewedUser) {
        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            XApplication.class.getResource(
                                    "/com/example/demo1/profile-view.fxml"
                            )
                    );

            Parent root = loader.load();

            ProfileViewController controller =
                    loader.getController();

            controller.setCurrentUser(currentUser);
            controller.setViewedUser(viewedUser);

            Scene scene =
                    new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Profile - Twitter");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}