package org.example;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class RegisterWindow {

    public void show(Stage stage) {
        stage.setTitle("Register");

        // ── Title ────────────────────────────────────────────
        Label titleLabel = new Label("Create Account");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        titleLabel.setTextFill(Color.web("#1a1a2e"));

        Label subtitleLabel = new Label("Fill in your details to get started");
        subtitleLabel.setFont(Font.font("Segoe UI", 14));
        subtitleLabel.setTextFill(Color.web("#6c757d"));

        // ── Form fields ──────────────────────────────────────
        Label userLabel = new Label("Username");
        userLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose a username");
        usernameField.setPrefHeight(40);
        styleTextField(usernameField);

        Label emailLabel = new Label("Email");
        emailLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        TextField emailField = new TextField();
        emailField.setPromptText("your@email.com");
        emailField.setPrefHeight(40);
        styleTextField(emailField);

        Label passLabel = new Label("Password");
        passLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Create a password");
        passwordField.setPrefHeight(40);
        styleTextField(passwordField);

        Label confirmLabel = new Label("Confirm Password");
        confirmLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Repeat your password");
        confirmField.setPrefHeight(40);
        styleTextField(confirmField);
        Label roleLabel = new Label("Register as");
        roleLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("MEMBER", "LIBRARIAN");
        roleBox.setValue("MEMBER");
        roleBox.setPrefHeight(40);
        roleBox.setMaxWidth(Double.MAX_VALUE);



        // ── Message label (errors + success) ─────────────────
        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("Segoe UI", 12));
        messageLabel.setVisible(false);
        messageLabel.setWrapText(true);

        // ── Buttons ──────────────────────────────────────────
        Button registerButton = new Button("Register");
        registerButton.setPrefWidth(Double.MAX_VALUE);
        registerButton.setPrefHeight(42);
        stylePrimaryButton(registerButton);

        Button backButton = new Button("← Back to Login");
        backButton.setPrefWidth(Double.MAX_VALUE);
        styleLinkButton(backButton);

        // ── Register logic ────────────────────────────────────
        registerButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String email    = emailField.getText().trim();
            String password = passwordField.getText();
            String confirm  = confirmField.getText();

            // Client-side validation
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                showMessage(messageLabel, "All fields are required.", false);
                return;
            }
            if (!email.contains("@") || !email.contains(".")) {
                showMessage(messageLabel, "Please enter a valid email address.", false);
                return;
            }
            if (!password.equals(confirm)) {
                showMessage(messageLabel, "Passwords do not match.", false);
                confirmField.clear();
                return;
            }
            if (password.length() < 4) {
                showMessage(messageLabel, "Password must be at least 4 characters.", false);
                return;
            }

            // Attempt to save via UserManager
            String error = UserManager.register(username, email, password, roleBox.getValue());
            if (error == null) {
                // Success — show message briefly then go back to login
                showMessage(messageLabel, "Account created! Redirecting to login…", true);
                registerButton.setDisable(true);
                new Thread(() -> {
                    try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                    javafx.application.Platform.runLater(() -> {
                        LoginWindow loginWindow = new LoginWindow();
                        loginWindow.show(stage);
                    });
                }).start();
            } else {
                showMessage(messageLabel, error, false);
            }
        });

        backButton.setOnAction(e -> {
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.show(stage);
        });

        // ── Layout ────────────────────────────────────────────
        VBox titleBox = new VBox(4, titleLabel, subtitleLabel);
        titleBox.setAlignment(Pos.CENTER);

        VBox form = new VBox(8,
                userLabel,    usernameField,
                emailLabel,   emailField,
                passLabel,    passwordField,
                confirmLabel, confirmField,
                roleLabel,    roleBox,
                messageLabel,
                registerButton,
                backButton
        );
        form.setPadding(new Insets(28, 36, 28, 36));
        form.setMaxWidth(380);
        form.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 20, 0, 0, 4);"
        );

        VBox page = new VBox(16, titleBox, form);
        page.setAlignment(Pos.CENTER);
        page.setPadding(new Insets(20));

        StackPane root = new StackPane(page);
        root.setStyle("-fx-background-color: #f0f2f5;");

        Scene scene = new Scene(root, 480, 680);
        stage.setScene(scene);
        stage.setResizable(false);
    }

    // ── Helpers ───────────────────────────────────────────────

    private void showMessage(Label label, String text, boolean success) {
        label.setText(text);
        label.setTextFill(Color.web(success ? "#198754" : "#dc3545"));
        label.setVisible(true);
    }

    private void styleTextField(Control field) {
        String base =
            "-fx-background-color: #f8f9fa;" +
            "-fx-border-color: #dee2e6;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 0 12 0 12;";
        field.setStyle(base);
        field.focusedProperty().addListener((obs, old, focused) -> {
            if (focused) {
                field.setStyle(base.replace("#f8f9fa", "#fff").replace("#dee2e6", "#4361ee"));
            } else {
                field.setStyle(base);
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
