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

public class MemberDashboard {

    private String loggedInUsername;
    public void show(Stage stage, String username) {

        this.loggedInUsername = username;
        stage.setTitle("Member Dashboard");

        // ── Header ────────────────────────────────────────────
        Label titleLabel = new Label("Library Management System");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.web("#1a1a2e"));

        Label subtitleLabel = new Label("Welcome, " + username + " (Member)");
        subtitleLabel.setFont(Font.font("Segoe UI", 14));
        subtitleLabel.setTextFill(Color.web("#6c757d"));

        // ── Buttons ───────────────────────────────────────────
        Button searchBtn = new Button("Search Catalog");
        Button checkoutBtn = new Button("Checkout Book");
        Button returnBtn = new Button("Return Book");
        Button reserveBtn = new Button("Reserve Book");
        Button renewBtn = new Button("Renew Book");
        Button removeReservationBtn = new Button("Remove Reservation");
        Button payFineBtn = new Button("Pay Fine");
        Button viewAccountBtn = new Button("View Account");
        Button logoutBtn = new Button("Log Out");

        for (Button btn : new Button[]{searchBtn, checkoutBtn, returnBtn,
                reserveBtn, renewBtn, removeReservationBtn,
                payFineBtn, viewAccountBtn}) {
            styleDashboardButton(btn);
        }
        styleLogoutButton(logoutBtn);

        // ── Button actions ────────────────────────────────────
        searchBtn.setOnAction(e -> showSearchDialog());
        checkoutBtn.setOnAction(e -> showCheckoutDialog());
        returnBtn.setOnAction(e -> showReturnDialog());
        reserveBtn.setOnAction(e -> showReserveDialog());
        renewBtn.setOnAction(e -> showRenewDialog());
        removeReservationBtn.setOnAction(e -> showRemoveReservationDialog());
        payFineBtn.setOnAction(e -> showPayFineDialog());
        viewAccountBtn.setOnAction(e -> showViewAccountDialog(username));
        logoutBtn.setOnAction(e -> {
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.show(stage);
        });

        // ── Layout ────────────────────────────────────────────
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        grid.add(searchBtn, 0, 0);
        grid.add(checkoutBtn, 1, 0);
        grid.add(returnBtn, 0, 1);
        grid.add(reserveBtn, 1, 1);
        grid.add(renewBtn, 0, 2);
        grid.add(removeReservationBtn, 1, 2);
        grid.add(payFineBtn, 0, 3);
        grid.add(viewAccountBtn, 1, 3);

        for (javafx.scene.Node node : grid.getChildren()) {
            GridPane.setFillWidth(node, true);
            ((Button) node).setMaxWidth(Double.MAX_VALUE);
        }

        VBox header = new VBox(4, titleLabel, subtitleLabel);
        header.setAlignment(Pos.CENTER);

        logoutBtn.setMaxWidth(Double.MAX_VALUE);

        VBox card = new VBox(20, header, grid, logoutBtn);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(36, 48, 36, 48));
        card.setMaxWidth(520);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 20, 0, 0, 4);"
        );

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: #f0f2f5;");
        root.setPadding(new Insets(24));

        Scene scene = new Scene(root, 560, 600);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    // ── Dialogs ───────────────────────────────────────────────

    private void showSearchDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Search Catalog");

        Label label = new Label("Search by:");
        ComboBox<String> searchType = new ComboBox<>();
        searchType.getItems().addAll("Title", "Author", "Subject");
        searchType.setValue("Title");

        TextField searchField = new TextField();
        searchField.setPromptText("Enter search term");

        // results area
        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefHeight(200);
        resultArea.setWrapText(true);
        resultArea.setPromptText("Results will appear here...");

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.web("#dc3545"));

        Button searchBtn = new Button("Search");
        stylePrimaryButton(searchBtn);
        searchBtn.setPrefWidth(Double.MAX_VALUE);

        searchBtn.setOnAction(e -> {
            String term = searchField.getText().trim();
            if (term.isEmpty()) {
                errorLabel.setText("Please enter a search term.");
                return;
            }

            errorLabel.setText("");
            Catalog catalog = LibraryData.getInstance().getCatalog();
            String result = "";

            switch (searchType.getValue()) {
                case "Title":
                    result = catalog.searchByTitle(term);
                    break;
                case "Author":
                    result = catalog.searchByAuthor(term);
                    break;
                case "Subject":
                    result = catalog.searchBySubject(term);
                    break;
            }

            resultArea.setText(result);
        });

        VBox layout = new VBox(12,
                label, searchType,
                searchField,
                searchBtn,
                errorLabel,
                resultArea
        );
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 400, 440));
        dialog.show();
    }

    private void showCheckoutDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Checkout Book");

        Label label = new Label("Enter book barcode:");
        TextField barcodeField = new TextField();
        barcodeField.setPromptText("e.g. BC001");

        Label resultLabel = new Label();
        resultLabel.setWrapText(true);

        Button checkoutBtn = new Button("Checkout");
        stylePrimaryButton(checkoutBtn);
        checkoutBtn.setPrefWidth(Double.MAX_VALUE);

        checkoutBtn.setOnAction(e -> {
            String barcode = barcodeField.getText().trim();
            if (barcode.isEmpty()) {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Please enter a barcode.");
                return;
            }

            BookItem book = LibraryData.getInstance().findBookByBarcode(barcode);
            if (book == null) {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Book not found with barcode: " + barcode);
                return;
            }

            Member member = LibraryData.getInstance().findMemberByUsername(loggedInUsername);
            if (member == null) {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Member account not found.");
                return;
            }

            boolean success = member.checkoutBook(book);
            if (success) {
                LendingManager.getInstance().addLending(barcode, loggedInUsername);
                LibraryData.getInstance().updateBook(book);
                LibraryData.getInstance().updateMember(member);
                resultLabel.setTextFill(Color.web("#198754"));
                resultLabel.setText("Checked out: " + book.getTitle() +
                        "\nBarcode: " + barcode +
                        "\nDue in 10 days.");
            }
        });

        VBox layout = new VBox(12, label, barcodeField, checkoutBtn, resultLabel);
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 340, 220));
        dialog.show();
    }

    private void showReturnDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Return Book");

        Label label = new Label("Enter book barcode:");
        TextField barcodeField = new TextField();
        barcodeField.setPromptText("Book barcode");

        Label resultLabel = new Label();
        resultLabel.setWrapText(true);

        Button returnBtn = new Button("Return");
        stylePrimaryButton(returnBtn);
        returnBtn.setPrefWidth(Double.MAX_VALUE);

        returnBtn.setOnAction(e -> {
            String barcode = barcodeField.getText().trim();
            if (barcode.isEmpty()) {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Please enter a barcode.");
                return;
            }

        });

        VBox layout = new VBox(12, label, barcodeField, returnBtn, resultLabel);
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 340, 200));
        dialog.show();
    }

    private void showReserveDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Reserve Book");

        Label label = new Label("Enter book barcode:");
        TextField barcodeField = new TextField();
        barcodeField.setPromptText("Book barcode");

        Label resultLabel = new Label();
        resultLabel.setWrapText(true);

        Button reserveBtn = new Button("Reserve");
        stylePrimaryButton(reserveBtn);
        reserveBtn.setPrefWidth(Double.MAX_VALUE);

        reserveBtn.setOnAction(e -> {
            String barcode = barcodeField.getText().trim();
            if (barcode.isEmpty()) {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Please enter a barcode.");
                return;
            }
            resultLabel.setTextFill(Color.web("#198754"));
            resultLabel.setText("Book reserved successfully!");
            barcodeField.clear();
        });

        VBox layout = new VBox(12, label, barcodeField, reserveBtn, resultLabel);
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 340, 200));
        dialog.show();
    }

    private void showRenewDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Renew Book");

        Label label = new Label("Enter book barcode:");
        TextField barcodeField = new TextField();
        barcodeField.setPromptText("Book barcode");

        Label resultLabel = new Label();
        resultLabel.setWrapText(true);

        Button renewBtn = new Button("Renew");
        stylePrimaryButton(renewBtn);
        renewBtn.setPrefWidth(Double.MAX_VALUE);

        renewBtn.setOnAction(e -> {
            String barcode = barcodeField.getText().trim();
            if (barcode.isEmpty()) {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Please enter a barcode.");
                return;
            }
            resultLabel.setTextFill(Color.web("#198754"));
            resultLabel.setText("Book renewed successfully!");
            barcodeField.clear();
        });

        VBox layout = new VBox(12, label, barcodeField, renewBtn, resultLabel);
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 340, 200));
        dialog.show();
    }

    private void showRemoveReservationDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Remove Reservation");

        Label label = new Label("Enter book barcode:");
        TextField barcodeField = new TextField();
        barcodeField.setPromptText("Book barcode");

        Label resultLabel = new Label();
        resultLabel.setWrapText(true);

        Button removeBtn = new Button("Remove Reservation");
        stylePrimaryButton(removeBtn);
        removeBtn.setPrefWidth(Double.MAX_VALUE);

        removeBtn.setOnAction(e -> {
            String barcode = barcodeField.getText().trim();
            if (barcode.isEmpty()) {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Please enter a barcode.");
                return;
            }
            resultLabel.setTextFill(Color.web("#198754"));
            resultLabel.setText("Reservation removed successfully!");
            barcodeField.clear();
        });

        VBox layout = new VBox(12, label, barcodeField, removeBtn, resultLabel);
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 340, 200));
        dialog.show();
    }

    private void showPayFineDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Pay Fine");

        Label label = new Label("Enter fine amount:");
        TextField fineField = new TextField();
        fineField.setPromptText("Amount in $");

        Label resultLabel = new Label();
        resultLabel.setWrapText(true);

        Button payBtn = new Button("Pay Fine");
        stylePrimaryButton(payBtn);
        payBtn.setPrefWidth(Double.MAX_VALUE);

        payBtn.setOnAction(e -> {
            String amount = fineField.getText().trim();
            if (amount.isEmpty()) {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Please enter an amount.");
                return;
            }
            try {
                double fine = Double.parseDouble(amount);
                resultLabel.setTextFill(Color.web("#198754"));
                resultLabel.setText("Fine of $" + fine + " paid successfully!");
                fineField.clear();
            } catch (NumberFormatException ex) {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Invalid amount.");
            }
        });

        VBox layout = new VBox(12, label, fineField, payBtn, resultLabel);
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 340, 200));
        dialog.show();
    }

    private void showViewAccountDialog(String username) {
        Stage dialog = new Stage();
        dialog.setTitle("View Account");

        Label nameLabel = new Label("Username: " + username);
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        Label roleLabel = new Label("Role: Member");
        roleLabel.setFont(Font.font("Segoe UI", 13));
        roleLabel.setTextFill(Color.web("#6c757d"));

        Label booksLabel = new Label("Books checked out: 0");
        booksLabel.setFont(Font.font("Segoe UI", 13));

        Button closeBtn = new Button("Close");
        stylePrimaryButton(closeBtn);
        closeBtn.setPrefWidth(Double.MAX_VALUE);
        closeBtn.setOnAction(e -> dialog.close());

        VBox layout = new VBox(14, nameLabel, roleLabel, booksLabel, closeBtn);
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 300, 200));
        dialog.show();
    }

    // ── Style helpers ─────────────────────────────────────────

    private void styleDashboardButton(Button button) {
        String base =
                "-fx-background-color: #4361ee;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 10 20 10 20;";
        button.setStyle(base);
        button.setOnMouseEntered(e -> button.setStyle(base + "-fx-background-color: #3451d1;"));
        button.setOnMouseExited(e -> button.setStyle(base));
    }

    private void stylePrimaryButton(Button button) {
        String base =
                "-fx-background-color: #4361ee;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;";
        button.setStyle(base);
        button.setOnMouseEntered(e -> button.setStyle(base + "-fx-background-color: #3451d1;"));
        button.setOnMouseExited(e -> button.setStyle(base));
    }

    private void styleLogoutButton(Button button) {
        String base =
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #dc3545;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-text-fill: #dc3545;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 8;";
        button.setStyle(base);
        button.setOnMouseEntered(e -> button.setStyle(
                base.replace("transparent", "#dc3545") + "-fx-text-fill: white;"
        ));
        button.setOnMouseExited(e -> button.setStyle(base));
    }
}