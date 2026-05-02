package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginWindow {

    public void show(Stage stage) {
        stage.setTitle("Login");

        Label titleLabel = new Label("Welcome Back");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        titleLabel.setTextFill(Color.web("#1a1a2e"));

        Label subtitleLabel = new Label("Sign in to your account");
        subtitleLabel.setFont(Font.font("Segoe UI", 14));
        subtitleLabel.setTextFill(Color.web("#6c757d"));

        Label userLabel = new Label("Username");
        userLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setPrefHeight(40);
        styleTextField(usernameField);

        Label passLabel = new Label("Password");
        passLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(40);
        styleTextField(passwordField);

        Label errorLabel = new Label();
        errorLabel.setFont(Font.font("Segoe UI", 12));
        errorLabel.setTextFill(Color.web("#dc3545"));
        errorLabel.setVisible(false);
        errorLabel.setWrapText(true);

        Button loginButton = new Button("Log In");
        loginButton.setPrefWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(42);
        stylePrimaryButton(loginButton);

        Button registerButton = new Button("Don't have an account? Register here");
        registerButton.setPrefWidth(Double.MAX_VALUE);
        registerButton.setFont(Font.font("Segoe UI", 13));
        styleLinkButton(registerButton);

        // ── Login logic ──────────────────────────────────────
        Runnable doLogin = () -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showError(errorLabel, "Please fill in all fields.");
                return;
            }

            String[] loginResult = UserManager.login(username, password);
            if (loginResult != null) {
                String loggedInUser = loginResult[0];
                String role = loginResult[1];
                errorLabel.setVisible(false);

                if (role.equalsIgnoreCase("LIBRARIAN")) {
                    LibrarianDashboard librarianDashboard = new LibrarianDashboard();
                    librarianDashboard.show(stage, loggedInUser);
                } else {
                    MemberDashboard memberDashboard = new MemberDashboard();
                    memberDashboard.show(stage, loggedInUser);
                }
            } else {
                showError(errorLabel, "Incorrect username or password.");
                passwordField.clear();
            }
        };

        loginButton.setOnAction(e -> doLogin.run());
        usernameField.setOnAction(e -> doLogin.run());
        passwordField.setOnAction(e -> doLogin.run());

        registerButton.setOnAction(e -> {
            RegisterWindow registerWindow = new RegisterWindow();
            registerWindow.show(stage);
        });

        VBox titleBox = new VBox(4, titleLabel, subtitleLabel);
        titleBox.setAlignment(Pos.CENTER);

        VBox form = new VBox(8,
                userLabel, usernameField,
                passLabel, passwordField,
                errorLabel,
                loginButton,
                registerButton
        );
        form.setPadding(new Insets(28, 36, 28, 36));
        form.setMaxWidth(380);
        form.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 20, 0, 0, 4);"
        );

        VBox titleSection = new VBox(16, titleBox, form);
        titleSection.setAlignment(Pos.CENTER);
        titleSection.setPadding(new Insets(20));

        StackPane root = new StackPane(titleSection);
        root.setStyle("-fx-background-color: #f0f2f5;");

        Scene scene = new Scene(root, 480, 520);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
    }

    private void styleTextField(Control field) {
        field.setStyle(
                "-fx-background-color: #f8f9fa;" +
                        "-fx-border-color: #dee2e6;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 0 12 0 12;"
        );
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                        "-fx-background-color: #fff;" +
                                "-fx-border-color: #4361ee;" +
                                "-fx-border-radius: 6;" +
                                "-fx-background-radius: 6;" +
                                "-fx-font-size: 13px;" +
                                "-fx-padding: 0 12 0 12;"
                );
            } else {
                field.setStyle(
                        "-fx-background-color: #f8f9fa;" +
                                "-fx-border-color: #dee2e6;" +
                                "-fx-border-radius: 6;" +
                                "-fx-background-radius: 6;" +
                                "-fx-font-size: 13px;" +
                                "-fx-padding: 0 12 0 12;"
                );
            }
        });
    }

    private void stylePrimaryButton(Button button) {
        String base =
                "-fx-background-color: #4361ee;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;";
        button.setStyle(base);
        button.setOnMouseEntered(e -> button.setStyle(base + "-fx-background-color: #3451d1;"));
        button.setOnMouseExited(e -> button.setStyle(base));
    }

    private void styleLinkButton(Button button) {
        String base =
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #4361ee;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: transparent;";
        button.setStyle(base);
        button.setOnMouseEntered(e -> button.setStyle(base + "-fx-text-fill: #3451d1; -fx-underline: true;"));
        button.setOnMouseExited(e -> button.setStyle(base));
    }
}