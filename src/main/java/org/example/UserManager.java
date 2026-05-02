package org.example;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles reading and writing user data to/from users.txt.
 * Each line format: username|password|email
 */
public class UserManager {

    private static final String FILE_NAME = "users.txt";
    private static final String SEPARATOR = "|";

    /**
     * Attempts to log in with the given credentials.
     * Returns the username string on success, or null on failure.
     */
    public static String[] login(String username, String password) {
        List<String[]> users = readAllUsers();
        for (String[] user : users) {
            if (user[0].equalsIgnoreCase(username) && user[1].equals(password)) {
                return new String[]{user[0], user[3]}; // username + role
            }
        }
        return null;
    }

    /**
     * Attempts to register a new user.
     * Returns null on success, or an error message string on failure.
     */
    public static String register(String username, String email, String password, String role) {
        List<String[]> users = readAllUsers();

        // Check for duplicate username or email (case-insensitive)
        for (String[] user : users) {
            if (user[0].equalsIgnoreCase(username)) {
                return "Username already exists. Please choose a different one.";
            }
            if (user[2].equalsIgnoreCase(email)) {
                return "An account with that email already exists.";
            }
        }

        // Append new user to file
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            pw.println(username + SEPARATOR + password + SEPARATOR + email + SEPARATOR + role);
            return null; // success
        } catch (IOException e) {
            return "Error saving user data: " + e.getMessage();
        }
    }

    /**
     * Reads all users from users.txt and returns them as a list of String arrays.
     * Each array: [username, password, email]
     */
    private static List<String[]> readAllUsers() {
        List<String[]> users = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return users;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length == 4) {
                    users.add(new String[]{
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim()
                    });
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users.txt: " + e.getMessage());
        }
        return users;
    }
}
