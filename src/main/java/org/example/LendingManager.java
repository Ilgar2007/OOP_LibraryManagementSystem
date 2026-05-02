package org.example;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LendingManager {

    private static LendingManager instance;
    private static final String DB_URL = "jdbc:sqlite:library.db";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private List<String[]> lendingRecords;

    private LendingManager() {
        this.lendingRecords = new ArrayList<>();
        initDatabase();
        loadLendings();
    }

    public static LendingManager getInstance() {
        if (instance == null) {
            instance = new LendingManager();
        }
        return instance;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS lendings (
                barcode     TEXT NOT NULL,
                username    TEXT NOT NULL,
                lend_date   TEXT NOT NULL,
                due_date    TEXT NOT NULL,
                return_date TEXT
            )""";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("LendingManager DB init error: " + e.getMessage());
        }
    }

    private void loadLendings() {
        String sql = "SELECT * FROM lendings";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String returnDate = rs.getString("return_date");
                lendingRecords.add(new String[]{
                        rs.getString("barcode"),
                        rs.getString("username"),
                        rs.getString("lend_date"),
                        rs.getString("due_date"),
                        returnDate == null ? "null" : returnDate
                });
            }
        } catch (SQLException e) {
            System.out.println("Error loading lendings: " + e.getMessage());
        }
    }

    // ── Add new lending ───────────────────────────────────────

    public void addLending(String barcode, String username) {
        Date today = new Date();
        long tenDays = 10L * 24 * 60 * 60 * 1000;
        Date dueDate = new Date(today.getTime() + tenDays);

        String lendDateStr = DATE_FORMAT.format(today);
        String dueDateStr  = DATE_FORMAT.format(dueDate);

        String sql = "INSERT INTO lendings (barcode, username, lend_date, due_date, return_date) VALUES (?, ?, ?, ?, NULL)";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, barcode);
            ps.setString(2, username);
            ps.setString(3, lendDateStr);
            ps.setString(4, dueDateStr);
            ps.executeUpdate();

            lendingRecords.add(new String[]{barcode, username, lendDateStr, dueDateStr, "null"});
            System.out.println("Lending record saved for: " + username);

        } catch (SQLException e) {
            System.out.println("Error adding lending: " + e.getMessage());
        }
    }

    // ── Return a book ─────────────────────────────────────────

    public boolean returnLending(String barcode, String username) {
        String returnDateStr = DATE_FORMAT.format(new Date());
        String sql = """
            UPDATE lendings SET return_date = ?
            WHERE barcode = ? AND username = ? AND return_date IS NULL""";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, returnDateStr);
            ps.setString(2, barcode);
            ps.setString(3, username);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                // update in-memory list
                for (String[] record : lendingRecords) {
                    if (record[0].equals(barcode) && record[1].equals(username) && record[4].equals("null")) {
                        record[4] = returnDateStr;
                        break;
                    }
                }
                System.out.println("Return recorded for: " + username);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error returning lending: " + e.getMessage());
        }
        System.out.println("No active lending found for barcode: " + barcode);
        return false;
    }

    // ── Check if overdue ──────────────────────────────────────

    public boolean isOverdue(String barcode, String username) {
        for (String[] record : lendingRecords) {
            if (record[0].equals(barcode) && record[1].equals(username) && record[4].equals("null")) {
                try {
                    Date dueDate = DATE_FORMAT.parse(record[3]);
                    return new Date().after(dueDate);
                } catch (ParseException e) {
                    System.out.println("Error parsing date.");
                }
            }
        }
        return false;
    }

    // ── Calculate fine amount ─────────────────────────────────

    public double calculateFine(String barcode, String username) {
        for (String[] record : lendingRecords) {
            if (record[0].equals(barcode) && record[1].equals(username) && record[4].equals("null")) {
                try {
                    Date dueDate = DATE_FORMAT.parse(record[3]);
                    Date today = new Date();
                    if (today.after(dueDate)) {
                        long diff = today.getTime() - dueDate.getTime();
                        long daysLate = diff / (1000 * 60 * 60 * 24);
                        return daysLate * 1.0;
                    }
                } catch (ParseException e) {
                    System.out.println("Error parsing date.");
                }
            }
        }
        return 0.0;
    }

    // ── Get all lendings for a user ───────────────────────────

    public List<String[]> getLendingsForUser(String username) {
        List<String[]> result = new ArrayList<>();
        for (String[] record : lendingRecords) {
            if (record[1].equals(username)) result.add(record);
        }
        return result;
    }

    // ── Get active lending for a barcode ──────────────────────

    public String[] getActiveLending(String barcode) {
        for (String[] record : lendingRecords) {
            if (record[0].equals(barcode) && record[4].equals("null")) return record;
        }
        return null;
    }
}
