package com.example.demo1.View;

import com.example.demo1.Controller.XControllers;
import com.example.demo1.Model.Admin; // ایمپورت مدل ادمین
import com.example.demo1.Model.User;
import com.example.demo1.XApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class LoginViewController {
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMessage;

    private XControllers.AuthController controller;

    @FXML
    private void initialize() {
        controller = new XControllers.AuthController();
    }

    // در LoginViewController.java
    @FXML
    public void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please fill all fields!", Color.RED);
            return;
        }

        // ۱. بررسی از طریق AuthController
        User user = authController.login(username, password);

        if (user != null) {
            // ۲. چک کردن قفل نبودن کاربر توسط ادمین (شرط پی‌دی‌اف فاز اول)
            if (user.isLocked()) {
                showMessage("This account has been suspended by Admin!", Color.RED);
                return;
            }

            // ورود موفقیت‌آمیز کاربر عادی به تایم‌لاین
            XApplication.showTimelineView(user);
        } else {
            showMessage("Invalid username or password.", Color.RED);
        }
    }

    @FXML
    public void handleAdminLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        // بررسی اطلاعات ادمین بر اساس الگوی Singleton دیتابیس پروژه
        if (com.example.demo1.Model.Admin.getInstance().login(username, password)) {
            XApplication.showAdminDashboard();
        } else {
            showMessage("Invalid Admin Credentials or Access Denied!", Color.RED);
        }
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        XApplication.showRegisterView();
    }

    private void showMessage(String message, Color color) {
        lblMessage.setText(message);
        lblMessage.setTextFill(color);
    }
}