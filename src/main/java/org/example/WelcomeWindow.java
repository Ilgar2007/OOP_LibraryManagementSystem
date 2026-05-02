package org.example;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class WelcomeWindow {

    public void show(Stage stage, String username) {
        stage.setTitle("Welcome");
        String initial = username.substring(0, 1).toUpperCase();
        Label avatarLabel = new Label(initial);
        avatarLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        avatarLabel.setTextFill(Color.WHITE);
        avatarLabel.setAlignment(Pos.CENTER);
        avatarLabel.setMinSize(80, 80);
        avatarLabel.setMaxSize(80, 80);
        avatarLabel.setStyle(
            "-fx-background-color: #4361ee;" +
            "-fx-background-radius: 40;"
        );


        Label welcomeLabel = new Label("Welcome, " + username + "!");
        welcomeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        welcomeLabel.setTextFill(Color.web("#1a1a2e"));

        Label subLabel = new Label("You have successfully logged in.");
        subLabel.setFont(Font.font("Segoe UI", 15));
        subLabel.setTextFill(Color.web("#6c757d"));


        Button logoutButton = new Button("Log Out");
        logoutButton.setPrefWidth(160);
        logoutButton.setPrefHeight(42);
        String base =
            "-fx-background-color: transparent;" +
            "-fx-border-color: #4361ee;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-text-fill: #4361ee;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 8;";
        logoutButton.setStyle(base);
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle(
            base.replace("transparent", "#4361ee") + "-fx-text-fill: white;"
        ));
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle(base));

        logoutButton.setOnAction(e -> {
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.show(stage);
        });


        VBox card = new VBox(18, avatarLabel, welcomeLabel, subLabel, logoutButton);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(48, 60, 48, 60));
        card.setMaxWidth(400);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 24, 0, 0, 6);"
        );

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: #f0f2f5;");

        Scene scene = new Scene(root, 480, 400);
        stage.setScene(scene);
        stage.setResizable(false);
    }
}
