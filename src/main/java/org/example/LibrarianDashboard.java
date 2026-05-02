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

import java.util.ArrayList;
import java.util.List;

public class LibrarianDashboard {

    public void show(Stage stage, String username) {
        stage.setTitle("Librarian Dashboard");

        // ── Header ────────────────────────────────────────────
        Label titleLabel = new Label("Library Management System");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.web("#1a1a2e"));

        Label subtitleLabel = new Label("Welcome, " + username + " (Librarian)");
        subtitleLabel.setFont(Font.font("Segoe UI", 14));
        subtitleLabel.setTextFill(Color.web("#6c757d"));

        // ── Buttons ───────────────────────────────────────────
        Button addBookBtn = new Button("Add Book Item");
        Button removeBookBtn = new Button("Remove Book");
        Button issueBookBtn = new Button("Issue Book");
        Button blockMemberBtn = new Button("Block Member");
        Button unblockMemberBtn = new Button("Unblock Member");
        Button issuecardBtn = new Button("Issue Library Card");
        Button searchBtn = new Button("Search Catalog");
        Button logoutBtn = new Button("Log Out");

        // style all buttons
        for (Button btn : new Button[]{addBookBtn, removeBookBtn,
                issueBookBtn, blockMemberBtn, unblockMemberBtn,
                issuecardBtn, searchBtn}) {
            styleDashboardButton(btn);
        }
        styleLogoutButton(logoutBtn);

        // ── Button actions ────────────────────────────────────
        addBookBtn.setOnAction(e -> showAddBookDialog(stage));
        removeBookBtn.setOnAction(e -> showRemoveBookDialog(stage));
        issueBookBtn.setOnAction(e -> showIssueBookDialog(stage));
        blockMemberBtn.setOnAction(e -> showBlockMemberDialog(stage, true));
        unblockMemberBtn.setOnAction(e -> showBlockMemberDialog(stage, false));
        issuecardBtn.setOnAction(e -> showIssueCardDialog(stage));
        searchBtn.setOnAction(e -> showSearchDialog(stage));
        logoutBtn.setOnAction(e -> {
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.show(stage);
        });

        // ── Layout ────────────────────────────────────────────
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        grid.add(addBookBtn, 0, 0);
        grid.add(removeBookBtn, 1, 0);
        grid.add(issueBookBtn, 0, 1);
        grid.add(blockMemberBtn, 1, 1);
        grid.add(unblockMemberBtn, 0, 2);
        grid.add(issuecardBtn, 1, 2);
        grid.add(searchBtn, 0, 3);

        for (javafx.scene.Node node : grid.getChildren()) {
            GridPane.setFillWidth(node, true);
            ((Button) node).setMaxWidth(Double.MAX_VALUE);
        }

        VBox header = new VBox(4, titleLabel, subtitleLabel);
        header.setAlignment(Pos.CENTER);

        VBox card = new VBox(20, header, grid, logoutBtn);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(36, 48, 36, 48));
        card.setMaxWidth(480);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 20, 0, 0, 4);"
        );

        logoutBtn.setMaxWidth(Double.MAX_VALUE);

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: #f0f2f5;");
        root.setPadding(new Insets(24));

        Scene scene = new Scene(root, 520, 580);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    // ── Dialogs ───────────────────────────────────────────────

    private void showAddBookDialog(Stage stage) {
        Stage dialog = new Stage();
        dialog.setTitle("Add Book Item");

        Label isbnLabel = new Label("ISBN:");
        TextField isbnField = new TextField();
        isbnField.setPromptText("Enter ISBN");

        Label titleLbl = new Label("Title:");
        TextField titleField = new TextField();
        titleField.setPromptText("Enter title");

        Label subjectLbl = new Label("Subject:");
        TextField subjectField = new TextField();
        subjectField.setPromptText("Enter subject");

        Label priceLbl = new Label("Price:");
        TextField priceField = new TextField();
        priceField.setPromptText("Enter price");

        Label formatLbl = new Label("Format:");
        ComboBox<BookFormat> formatBox = new ComboBox<>();
        formatBox.getItems().addAll(BookFormat.values());
        formatBox.setValue(BookFormat.HARDCOVER);

        Label resultLabel = new Label();
        resultLabel.setTextFill(Color.web("#198754"));

        Button addBtn = new Button("Add");
        stylePrimaryButton(addBtn);
        addBtn.setPrefWidth(Double.MAX_VALUE);

        addBtn.setOnAction(e -> {
            try {
                String isbn = isbnField.getText().trim();
                String title = titleField.getText().trim();
                String subject = subjectField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                BookFormat format = formatBox.getValue();

                if (isbn.isEmpty() || title.isEmpty() || subject.isEmpty()) {
                    resultLabel.setTextFill(Color.web("#dc3545"));
                    resultLabel.setText("Please fill in all fields.");
                    return;
                }

                // generate a unique barcode
                String barcode = "BC" + (LibraryData.getInstance().getAllBooks().size() + 1);

                List<Author> authors = new ArrayList<>();

                BookItem newBook = new BookItem(
                        isbn,
                        title,
                        subject,
                        "Unknown",
                        "English",
                        0,
                        authors,
                        barcode,
                        false,
                        price,
                        format,
                        BookStatus.AVAILABLE,
                        new java.util.Date(),
                        new java.util.Date()
                );

                LibraryData.getInstance().addBook(newBook);

                resultLabel.setTextFill(Color.web("#198754"));
                resultLabel.setText("Book added! Barcode: " + barcode);
                isbnField.clear();
                titleField.clear();
                subjectField.clear();
                priceField.clear();

            } catch (NumberFormatException ex) {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Invalid price format.");
            }
        });

        VBox layout = new VBox(10,
                isbnLabel, isbnField,
                titleLbl, titleField,
                subjectLbl, subjectField,
                priceLbl, priceField,
                formatLbl, formatBox,
                addBtn, resultLabel
        );
        layout.setPadding(new Insets(24));
        layout.setMaxWidth(360);

        Scene dialogScene = new Scene(layout, 380, 460);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void showRemoveBookDialog(Stage stage) {
        Stage dialog = new Stage();
        dialog.setTitle("Remove Book");

        Label label = new Label("Enter book title to remove:");
        TextField titleField = new TextField();
        titleField.setPromptText("Book title");

        Label resultLabel = new Label();

        Button removeBtn = new Button("Remove");
        stylePrimaryButton(removeBtn);
        removeBtn.setPrefWidth(Double.MAX_VALUE);

        removeBtn.setOnAction(e -> {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Please enter a title.");
                return;
            }
            resultLabel.setTextFill(Color.web("#198754"));
            resultLabel.setText("Book removed: " + title);
            titleField.clear();
        });

        VBox layout = new VBox(12, label, titleField, removeBtn, resultLabel);
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 340, 200));
        dialog.show();
    }

    private void showIssueBookDialog(Stage stage) {
        Stage dialog = new Stage();
        dialog.setTitle("Issue Book");

        Label memberLabel = new Label("Member username:");
        TextField memberField = new TextField();
        memberField.setPromptText("Enter member username");

        Label bookLabel = new Label("Book barcode:");
        TextField bookField = new TextField();
        bookField.setPromptText("Enter book barcode");

        Label resultLabel = new Label();

        Button issueBtn = new Button("Issue");
        stylePrimaryButton(issueBtn);
        issueBtn.setPrefWidth(Double.MAX_VALUE);

        issueBtn.setOnAction(e -> {
            String member = memberField.getText().trim();
            String barcode = bookField.getText().trim();
            if (member.isEmpty() || barcode.isEmpty()) {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Please fill in all fields.");
                return;
            }
            resultLabel.setTextFill(Color.web("#198754"));
            resultLabel.setText("Book issued to: " + member);
            memberField.clear();
            bookField.clear();
        });

        VBox layout = new VBox(12,
                memberLabel, memberField,
                bookLabel, bookField,
                issueBtn, resultLabel
        );
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 340, 260));
        dialog.show();
    }

    private void showBlockMemberDialog(Stage stage, boolean block) {
        Stage dialog = new Stage();
        dialog.setTitle(block ? "Block Member" : "Unblock Member");

        Label label = new Label("Enter member username:");
        TextField memberField = new TextField();
        memberField.setPromptText("Member username");

        Label resultLabel = new Label();

        Button actionBtn = new Button(block ? "Block" : "Unblock");
        stylePrimaryButton(actionBtn);
        actionBtn.setPrefWidth(Double.MAX_VALUE);

        actionBtn.setOnAction(e -> {
            String member = memberField.getText().trim();
            if (member.isEmpty()) {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Please enter a username.");
                return;
            }
            resultLabel.setTextFill(Color.web("#198754"));
            resultLabel.setText("Member " + (block ? "blocked: " : "unblocked: ") + member);
            memberField.clear();
        });

        VBox layout = new VBox(12, label, memberField, actionBtn, resultLabel);
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 340, 200));
        dialog.show();
    }

    private void showIssueCardDialog(Stage stage) {
        Stage dialog = new Stage();
        dialog.setTitle("Issue Library Card");

        Label label = new Label("Enter member username:");
        TextField memberField = new TextField();
        memberField.setPromptText("Member username");

        Label resultLabel = new Label();

        Button issueBtn = new Button("Issue Card");
        stylePrimaryButton(issueBtn);
        issueBtn.setPrefWidth(Double.MAX_VALUE);

        issueBtn.setOnAction(e -> {
            String member = memberField.getText().trim();
            if (member.isEmpty()) {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Please enter a username.");
                return;
            }
            resultLabel.setTextFill(Color.web("#198754"));
            resultLabel.setText("Library card issued to: " + member);
            memberField.clear();
        });

        VBox layout = new VBox(12, label, memberField, issueBtn, resultLabel);
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 340, 200));
        dialog.show();
    }

    private void showSearchDialog(Stage stage) {
        Stage dialog = new Stage();
        dialog.setTitle("Search Catalog");

        Label label = new Label("Search by:");
        ComboBox<String> searchType = new ComboBox<>();
        searchType.getItems().addAll("Title", "Author", "Subject", "Publication Date");
        searchType.setValue("Title");

        TextField searchField = new TextField();
        searchField.setPromptText("Enter search term");

        Label resultLabel = new Label();
        resultLabel.setWrapText(true);

        Button searchBtn = new Button("Search");
        stylePrimaryButton(searchBtn);
        searchBtn.setPrefWidth(Double.MAX_VALUE);

        searchBtn.setOnAction(e -> {
            String term = searchField.getText().trim();
            if (term.isEmpty()) {
                resultLabel.setTextFill(Color.web("#dc3545"));
                resultLabel.setText("Please enter a search term.");
                return;
            }
            resultLabel.setTextFill(Color.web("#198754"));
            resultLabel.setText("Searching for " + searchType.getValue() + ": " + term);
        });

        VBox layout = new VBox(12,
                label, searchType,
                searchField,
                searchBtn, resultLabel
        );
        layout.setPadding(new Insets(24));

        dialog.setScene(new Scene(layout, 340, 260));
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