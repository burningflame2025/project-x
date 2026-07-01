package com.example.demo1.View;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import com.example.demo1.XApplication;
import com.example.demo1.Controller.XControllers;

public class RegisterViewController {
    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private ChoiceBox<String> choiceAccountType;
    @FXML private Label lblMessage;

    private XControllers.AuthController authController;

    @FXML
    public void initialize() {
        authController = new XControllers.AuthController();
        // اصلاح طبق مستندات: اطمینان از مقداردهی اولیه درست ChoiceBox
        choiceAccountType.getItems().setAll("Normal", "Blue", "Gold");
        choiceAccountType.setValue("Normal");
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        String firstname = txtFirstName.getText().trim();
        String lastname = txtLastName.getText().trim();
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String password = txtPassword.getText().trim();
        String confirmedPassword = txtConfirmPassword.getText().trim();
        String accountType = choiceAccountType.getValue();

        // 1. اعتبارسنجی کامل فیلدها (طبق پی‌دی‌اف، تمام فیلدها باید اجباری باشند)
        if (firstname.isEmpty() || lastname.isEmpty() || username.isEmpty() ||
                email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            showMessage("All fields are required!", Color.RED);
            return;
        }

        // 2. تطابق پسورد
        if (!password.equals(confirmedPassword)) {
            showMessage("Passwords do not match!", Color.RED);
            return;
        }

        // 3. استفاده از اعتبارسنج‌های کنترلر
        if (!authController.isValidPassword(password)) {
            showMessage("Password must be at least 8 characters, with letters and numbers.", Color.RED);
            return;
        }
        if (!authController.isValidEmail(email)) {
            showMessage("Invalid email format!", Color.RED);
            return;
        }
        if (!authController.isValidPhone(phone)) {
            showMessage("Invalid phone number format!", Color.RED);
            return;
        }

        choiceAccountType.getItems().setAll("Normal", "Blue", "Gold");
        String result = authController.register(username, password, email, phone, firstname, lastname, choiceAccountType.getValue());

        if (result.startsWith("Success")) {
            // نمایش پیام موفقیت‌آمیز به کاربر قبل از تغییر صفحه
            showMessage("Registration successful!", Color.GREEN);
            XApplication.showLoginView();
        } else {
            // نمایش خطاهای احتمالی از سمت کنترلر (مثلاً نام کاربری تکراری)
            showMessage(result, Color.RED);
        }
    }

    @FXML
    public void handleBack(ActionEvent event) {
        XApplication.showLoginView();
    }

    private void showMessage(String message, Color color) {
        lblMessage.setText(message);
        lblMessage.setTextFill(color);
    }
}