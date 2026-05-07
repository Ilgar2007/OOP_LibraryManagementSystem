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

        Label titleLabel = new Label("Library Management System");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.web("#1a1a2e"));

        Label subtitleLabel = new Label("Welcome, " + username + " (Member)");
        subtitleLabel.setFont(Font.font("Segoe UI", 14));
        subtitleLabel.setTextFill(Color.web("#6c757d"));

        Button searchBtn           = new Button("Search Catalog");
        Button checkoutBtn         = new Button("Checkout Book");
        Button returnBtn           = new Button("Return Book");
        Button reserveBtn          = new Button("Reserve Book");
        Button renewBtn            = new Button("Renew Book");
        Button removeReservationBtn = new Button("Remove Reservation");
        Button payFineBtn          = new Button("Pay Fine");
        Button viewAccountBtn      = new Button("View Account");
        Button logoutBtn           = new Button("Log Out");

        for (Button btn : new Button[]{searchBtn, checkoutBtn, returnBtn,
                reserveBtn, renewBtn, removeReservationBtn,
                payFineBtn, viewAccountBtn}) {
            styleDashboardButton(btn);
        }
        styleLogoutButton(logoutBtn);

        searchBtn.setOnAction(e -> showSearchDialog());
        checkoutBtn.setOnAction(e -> showCheckoutDialog());
        returnBtn.setOnAction(e -> showReturnDialog());
        reserveBtn.setOnAction(e -> showReserveDialog());
        renewBtn.setOnAction(e -> showRenewDialog());
        removeReservationBtn.setOnAction(e -> showRemoveReservationDialog());
        payFineBtn.setOnAction(e -> showPayFineDialog());
        viewAccountBtn.setOnAction(e -> showViewAccountDialog());
        logoutBtn.setOnAction(e -> {
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.show(stage);
        });

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

    // ── Search ────────────────────────────────────────────────

    private void showSearchDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Search Catalog");

        Label label = new Label("Search by:");
        ComboBox<String> searchType = new ComboBox<>();
        searchType.getItems().addAll("Title", "Author", "Subject");
        searchType.setValue("Title");

        TextField searchField = new TextField();
        searchField.setPromptText("Enter search term");

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

        VBox layout = new VBox(12, label, searchType, searchField, searchBtn, errorLabel, resultArea);
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 400, 460));
        dialog.show();
    }

    // ── Checkout ──────────────────────────────────────────────

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

            // check unpaid fines first
            if (FineManager.getInstance().hasUnpaidFines(loggedInUsername)) {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("You have unpaid fines! Please pay them before checking out.");
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
            } else {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Checkout failed. Book may not be available.");
            }
            barcodeField.clear();
        });

        VBox layout = new VBox(12, label, barcodeField, checkoutBtn, resultLabel);
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 340, 240));
        dialog.show();
    }

    // ── Return ────────────────────────────────────────────────

    private void showReturnDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Return Book");

        Label label = new Label("Enter book barcode:");
        TextField barcodeField = new TextField();
        barcodeField.setPromptText("e.g. BC001");

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

            // calculate fine before returning
            double fineAmount = LendingManager.getInstance()
                    .calculateFine(barcode, loggedInUsername);

            boolean success = member.returnBook(book);
            if (success) {
                LendingManager.getInstance().returnLending(barcode, loggedInUsername);
                LibraryData.getInstance().updateBook(book);
                LibraryData.getInstance().updateMember(member);

                if (fineAmount > 0) {
                    FineManager.getInstance().addFine(loggedInUsername, barcode, fineAmount);
                    resultLabel.setTextFill(Color.web("#dc3545"));
                    resultLabel.setText("Book returned but overdue!\nFine: $" + fineAmount +
                            "\nPlease pay your fine.");
                } else {
                    resultLabel.setTextFill(Color.web("#198754"));
                    resultLabel.setText("Book returned successfully: " + book.getTitle());
                }
            } else {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Return failed.");
            }
            barcodeField.clear();
        });

        VBox layout = new VBox(12, label, barcodeField, returnBtn, resultLabel);
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 340, 240));
        dialog.show();
    }

    // ── Reserve ───────────────────────────────────────────────

    private void showReserveDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Reserve Book");

        Label label = new Label("Enter book barcode:");
        TextField barcodeField = new TextField();
        barcodeField.setPromptText("e.g. BC001");

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

            boolean success = member.reserveBook(book);
            if (success) {
                ReservationManager.getInstance().addReservation(barcode, loggedInUsername);
                LibraryData.getInstance().updateBook(book);
                resultLabel.setTextFill(Color.web("#198754"));
                resultLabel.setText("Book reserved: " + book.getTitle());
            } else {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Reservation failed. Book may not be available.");
            }
            barcodeField.clear();
        });

        VBox layout = new VBox(12, label, barcodeField, reserveBtn, resultLabel);
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 340, 220));
        dialog.show();
    }

    // ── Renew ─────────────────────────────────────────────────

    private void showRenewDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Renew Book");

        Label label = new Label("Enter book barcode:");
        TextField barcodeField = new TextField();
        barcodeField.setPromptText("e.g. BC001");

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

            // check if reserved by another member
            if (ReservationManager.getInstance().hasActiveReservation(barcode)) {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Cannot renew — book is reserved by another member!");
                return;
            }

            // check fine before renewing
            double fineAmount = LendingManager.getInstance()
                    .calculateFine(barcode, loggedInUsername);
            if (fineAmount > 0) {
                FineManager.getInstance().addFine(loggedInUsername, barcode, fineAmount);
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Overdue fine: $" + fineAmount +
                        "\nPlease pay your fine before renewing.");
                return;
            }

            boolean success = member.renewBook(book);
            if (success) {
                LibraryData.getInstance().updateBook(book);
                resultLabel.setTextFill(Color.web("#198754"));
                resultLabel.setText("Book renewed: " + book.getTitle() +
                        "\nNew due date extended by 10 days.");
            } else {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Renewal failed.");
            }
            barcodeField.clear();
        });

        VBox layout = new VBox(12, label, barcodeField, renewBtn, resultLabel);
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 340, 240));
        dialog.show();
    }

    // ── Remove Reservation ────────────────────────────────────

    private void showRemoveReservationDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Remove Reservation");

        Label label = new Label("Enter book barcode:");
        TextField barcodeField = new TextField();
        barcodeField.setPromptText("e.g. BC001");

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

            BookItem book = LibraryData.getInstance().findBookByBarcode(barcode);
            if (book == null) {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Book not found with barcode: " + barcode);
                return;
            }

            boolean success = ReservationManager.getInstance()
                    .cancelReservation(barcode, loggedInUsername);
            if (success) {
                book.setStatus(BookStatus.AVAILABLE);
                LibraryData.getInstance().updateBook(book);
                resultLabel.setTextFill(Color.web("#198754"));
                resultLabel.setText("Reservation canceled for: " + book.getTitle());
            } else {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("No active reservation found for this book.");
            }
            barcodeField.clear();
        });

        VBox layout = new VBox(12, label, barcodeField, removeBtn, resultLabel);
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 340, 220));
        dialog.show();
    }

    // ── Pay Fine ──────────────────────────────────────────────

    private void showPayFineDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Pay Fine");

        // show total unpaid fines first
        double totalFine = FineManager.getInstance()
                .getTotalUnpaidAmount(loggedInUsername);

        Label totalLabel = new Label("Total unpaid fines: $" + totalFine);
        totalLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        totalLabel.setTextFill(totalFine > 0 ?
                Color.web("#dc3545") : Color.web("#198754"));

        Label label = new Label("Enter book barcode to pay fine for:");
        TextField barcodeField = new TextField();
        barcodeField.setPromptText("e.g. BC001");

        Label resultLabel = new Label();
        resultLabel.setWrapText(true);

        Button payBtn = new Button("Pay Fine");
        stylePrimaryButton(payBtn);
        payBtn.setPrefWidth(Double.MAX_VALUE);

        payBtn.setOnAction(e -> {
            String barcode = barcodeField.getText().trim();
            if (barcode.isEmpty()) {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Please enter a barcode.");
                return;
            }

            boolean success = FineManager.getInstance()
                    .payFine(loggedInUsername, barcode);
            if (success) {
                double remaining = FineManager.getInstance()
                        .getTotalUnpaidAmount(loggedInUsername);
                resultLabel.setTextFill(Color.web("#198754"));
                resultLabel.setText("Fine paid successfully!\nRemaining fines: $" + remaining);
                totalLabel.setText("Total unpaid fines: $" + remaining);
                totalLabel.setTextFill(remaining > 0 ?
                        Color.web("#dc3545") : Color.web("#198754"));
            } else {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("No unpaid fine found for this barcode.");
            }
            barcodeField.clear();
        });

        VBox layout = new VBox(12, totalLabel, label, barcodeField, payBtn, resultLabel);
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 360, 260));
        dialog.show();
    }

    // ── View Account ──────────────────────────────────────────

    private void showViewAccountDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("My Account");

        Member member = LibraryData.getInstance().findMemberByUsername(loggedInUsername);

        Label nameLabel = new Label("Username: " + loggedInUsername);
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));

        Label roleLabel = new Label("Role: Member");
        roleLabel.setTextFill(Color.web("#6c757d"));

        Label statusLabel = new Label("Status: " +
                (member != null ? member.getStatus() : "ACTIVE"));
        statusLabel.setTextFill(Color.web("#6c757d"));

        Label booksLabel = new Label("Books checked out: " +
                (member != null ? member.getTotalBooksCheckedOut() : 0));

        double totalFine = FineManager.getInstance().getTotalUnpaidAmount(loggedInUsername);
        Label fineLabel = new Label("Unpaid fines: $" + totalFine);
        fineLabel.setTextFill(totalFine > 0 ?
                Color.web("#dc3545") : Color.web("#198754"));

        // show active lendings
        Label lendingsTitle = new Label("Active lendings:");
        lendingsTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));

        TextArea lendingsArea = new TextArea();
        lendingsArea.setEditable(false);
        lendingsArea.setPrefHeight(120);
        lendingsArea.setWrapText(true);

        StringBuilder lendingsText = new StringBuilder();
        for (String[] record : LendingManager.getInstance()
                .getLendingsForUser(loggedInUsername)) {
            if (record[4].equals("null") || record[4].equals("Not returned")) {
                lendingsText.append("Barcode: ").append(record[0])
                        .append(" | Due: ").append(record[3]).append("\n");
            }
        }
        lendingsArea.setText(lendingsText.length() > 0 ?
                lendingsText.toString() : "No active lendings.");

        // show active reservations
        Label reservationsTitle = new Label("Active reservations:");
        reservationsTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));

        TextArea reservationsArea = new TextArea();
        reservationsArea.setEditable(false);
        reservationsArea.setPrefHeight(100);
        reservationsArea.setWrapText(true);

        StringBuilder reservationsText = new StringBuilder();
        for (String[] record : ReservationManager.getInstance()
                .getReservationsForUser(loggedInUsername)) {
            if (record[3].equals(ReservationStatus.WAITING.toString())) {
                reservationsText.append("Barcode: ").append(record[0])
                        .append(" | Date: ").append(record[2]).append("\n");
            }
        }
        reservationsArea.setText(reservationsText.length() > 0 ?
                reservationsText.toString() : "No active reservations.");

        Button closeBtn = new Button("Close");
        stylePrimaryButton(closeBtn);
        closeBtn.setPrefWidth(Double.MAX_VALUE);
        closeBtn.setOnAction(e -> dialog.close());

        VBox layout = new VBox(10,
                nameLabel, roleLabel, statusLabel, booksLabel, fineLabel,
                lendingsTitle, lendingsArea,
                reservationsTitle, reservationsArea,
                closeBtn
        );
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 380, 560));
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