package org.example;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FineManager {

    private static FineManager instance;
    private static final String DB_URL = "jdbc:sqlite:library.db";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private List<String[]> fineRecords;

    private FineManager() {
        this.fineRecords = new ArrayList<>();
        initDatabase();
        loadFines();
    }

    public static FineManager getInstance() {
        if (instance == null) {
            instance = new FineManager();
        }
        return instance;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS fines (
                username   TEXT NOT NULL,
                barcode    TEXT NOT NULL,
                amount     REAL NOT NULL,
                paid       INTEGER NOT NULL DEFAULT 0,
                fine_date  TEXT NOT NULL
            )""";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("FineManager DB init error: " + e.getMessage());
        }
    }

    private void loadFines() {
        String sql = "SELECT * FROM fines";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                fineRecords.add(new String[]{
                        rs.getString("username"),
                        rs.getString("barcode"),
                        String.valueOf(rs.getDouble("amount")),
                        rs.getInt("paid") == 1 ? "true" : "false",
                        rs.getString("fine_date")
                });
            }
        } catch (SQLException e) {
            System.out.println("Error loading fines: " + e.getMessage());
        }
    }



    public void addFine(String username, String barcode, double amount) {
        String dateStr = DATE_FORMAT.format(new Date());
        String sql = "INSERT INTO fines (username, barcode, amount, paid, fine_date) VALUES (?, ?, ?, 0, ?)";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, barcode);
            ps.setDouble(3, amount);
            ps.setString(4, dateStr);
            ps.executeUpdate();

            fineRecords.add(new String[]{username, barcode, String.valueOf(amount), "false", dateStr});
            System.out.println("Fine of $" + amount + " added for: " + username);

        } catch (SQLException e) {
            System.out.println("Error adding fine: " + e.getMessage());
        }
    }



    public boolean payFine(String username, String barcode) {
        String sql = "UPDATE fines SET paid = 1 WHERE username = ? AND barcode = ? AND paid = 0";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, barcode);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                for (String[] record : fineRecords) {
                    if (record[0].equals(username) && record[1].equals(barcode) && record[3].equals("false")) {
                        record[3] = "true";
                        break;
                    }
                }
                System.out.println("Fine paid for: " + username);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error paying fine: " + e.getMessage());
        }
        System.out.println("No unpaid fine found.");
        return false;
    }



    public List<String[]> getUnpaidFines(String username) {
        List<String[]> result = new ArrayList<>();
        for (String[] record : fineRecords) {
            if (record[0].equals(username) && record[3].equals("false")) result.add(record);
        }
        return result;
    }



    public double getTotalUnpaidAmount(String username) {
        double total = 0.0;
        for (String[] record : fineRecords) {
            if (record[0].equals(username) && record[3].equals("false")) {
                total += Double.parseDouble(record[2]);
            }
        }
        return total;
    }



    public boolean hasUnpaidFines(String username) {
        for (String[] record : fineRecords) {
            if (record[0].equals(username) && record[3].equals("false")) return true;
        }
        return false;
    }
}
