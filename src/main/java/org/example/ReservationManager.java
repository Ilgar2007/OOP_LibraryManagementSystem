package org.example;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationManager {

    private static ReservationManager instance;
    private static final String DB_URL = "jdbc:sqlite:library.db";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private List<String[]> reservationRecords;

    private ReservationManager() {
        this.reservationRecords = new ArrayList<>();
        initDatabase();
        loadReservations();
    }

    public static ReservationManager getInstance() {
        if (instance == null) {
            instance = new ReservationManager();
        }
        return instance;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS reservations (
                barcode          TEXT NOT NULL,
                username         TEXT NOT NULL,
                reservation_date TEXT NOT NULL,
                status           TEXT NOT NULL
            )""";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("ReservationManager DB init error: " + e.getMessage());
        }
    }

    private void loadReservations() {
        String sql = "SELECT * FROM reservations";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                reservationRecords.add(new String[]{
                        rs.getString("barcode"),
                        rs.getString("username"),
                        rs.getString("reservation_date"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            System.out.println("Error loading reservations: " + e.getMessage());
        }
    }

    // ── Add new reservation ───────────────────────────────────

    public void addReservation(String barcode, String username) {
        String dateStr = DATE_FORMAT.format(new Date());
        String status  = ReservationStatus.WAITING.toString();
        String sql = "INSERT INTO reservations (barcode, username, reservation_date, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, barcode);
            ps.setString(2, username);
            ps.setString(3, dateStr);
            ps.setString(4, status);
            ps.executeUpdate();

            reservationRecords.add(new String[]{barcode, username, dateStr, status});
            System.out.println("Reservation saved for: " + username);

        } catch (SQLException e) {
            System.out.println("Error adding reservation: " + e.getMessage());
        }
    }

    // ── Cancel reservation ────────────────────────────────────

    public boolean cancelReservation(String barcode, String username) {
        String canceled = ReservationStatus.CANCELED.toString();
        String waiting  = ReservationStatus.WAITING.toString();
        String sql = "UPDATE reservations SET status = ? WHERE barcode = ? AND username = ? AND status = ?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, canceled);
            ps.setString(2, barcode);
            ps.setString(3, username);
            ps.setString(4, waiting);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                for (String[] record : reservationRecords) {
                    if (record[0].equals(barcode) && record[1].equals(username) && record[3].equals(waiting)) {
                        record[3] = canceled;
                        break;
                    }
                }
                System.out.println("Reservation canceled for: " + username);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error canceling reservation: " + e.getMessage());
        }
        System.out.println("No active reservation found.");
        return false;
    }

    // ── Complete reservation ──────────────────────────────────

    public boolean completeReservation(String barcode, String username) {
        String completed = ReservationStatus.COMPLETED.toString();
        String waiting   = ReservationStatus.WAITING.toString();
        String sql = "UPDATE reservations SET status = ? WHERE barcode = ? AND username = ? AND status = ?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, completed);
            ps.setString(2, barcode);
            ps.setString(3, username);
            ps.setString(4, waiting);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                for (String[] record : reservationRecords) {
                    if (record[0].equals(barcode) && record[1].equals(username) && record[3].equals(waiting)) {
                        record[3] = completed;
                        break;
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error completing reservation: " + e.getMessage());
        }
        return false;
    }

    // ── Check if book has active reservation ──────────────────

    public boolean hasActiveReservation(String barcode) {
        for (String[] record : reservationRecords) {
            if (record[0].equals(barcode) && record[3].equals(ReservationStatus.WAITING.toString())) return true;
        }
        return false;
    }

    // ── Get all reservations for a user ───────────────────────

    public List<String[]> getReservationsForUser(String username) {
        List<String[]> result = new ArrayList<>();
        for (String[] record : reservationRecords) {
            if (record[1].equals(username)) result.add(record);
        }
        return result;
    }
}
