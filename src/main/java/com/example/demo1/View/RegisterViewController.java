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

        if (firstname.isEmpty() || lastname.isEmpty() || username.isEmpty() ||
                email.isEmpty() || phone.isEmpty() || password.isEmpty() || accountType == null) {
            showMessage("All fields are required!", Color.RED);
            return;
        }

        if (!password.equals(confirmedPassword)) {
            showMessage("Passwords do not match!", Color.RED);
            return;
        }

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
        String result = authController.register(username, password, email, phone, firstname, lastname, accountType);

        if (result.startsWith("Success")) {
            showMessage("Registration successful!", Color.GREEN);
            XApplication.showLoginView();
        } else {
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