package org.example;

import java.sql.*;

public class UserManager {

    private static final String DB_URL = "jdbc:sqlite:library.db";

    static {
        initDatabase();
    }

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private static void initDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                username TEXT PRIMARY KEY,
                password TEXT NOT NULL,
                email    TEXT NOT NULL UNIQUE,
                role     TEXT NOT NULL
            )""";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("UserManager DB init error: " + e.getMessage());
        }
    }

    // ── Login ─────────────────────────────────────────────────

    public static String[] login(String username, String password) {
        String sql = "SELECT username, role FROM users WHERE LOWER(username) = LOWER(?) AND password = ?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new String[]{rs.getString("username"), rs.getString("role")};
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return null;
    }

    // ── Register ──────────────────────────────────────────────

    public static String register(String username, String email, String password, String role) {
        // Check for duplicate username
        String checkUser  = "SELECT username FROM users WHERE LOWER(username) = LOWER(?)";
        String checkEmail = "SELECT email FROM users WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = connect()) {

            try (PreparedStatement ps = conn.prepareStatement(checkUser)) {
                ps.setString(1, username);
                if (ps.executeQuery().next()) return "Username already exists. Please choose a different one.";
            }

            try (PreparedStatement ps = conn.prepareStatement(checkEmail)) {
                ps.setString(1, email);
                if (ps.executeQuery().next()) return "An account with that email already exists.";
            }

            String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, email);
                ps.setString(4, role);
                ps.executeUpdate();
            }

            return null; // success

        } catch (SQLException e) {
            return "Error saving user data: " + e.getMessage();
        }
    }
}
