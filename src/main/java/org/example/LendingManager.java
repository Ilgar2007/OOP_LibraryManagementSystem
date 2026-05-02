package org.example;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class LendingManager {

    private static LendingManager instance;
    private static final String FILE_NAME = "lendings.txt";
    private static final String SEPARATOR = "|";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private List<BookLending> lendings;
    private List<String[]> lendingRecords; // stores raw data for file operations

    private LendingManager() {
        this.lendings = new ArrayList<>();
        this.lendingRecords = new ArrayList<>();
        loadLendings();
    }

    public static LendingManager getInstance() {
        if (instance == null) {
            instance = new LendingManager();
        }
        return instance;
    }

    // ── Load from file ────────────────────────────────────────

    private void loadLendings() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length < 5) continue;
                lendingRecords.add(parts);
            }
        } catch (IOException e) {
            System.out.println("Error reading lendings.txt: " + e.getMessage());
        }
    }

    // ── Save to file ──────────────────────────────────────────

    private void saveLendings() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME, false))) {
            for (String[] record : lendingRecords) {
                pw.println(String.join(SEPARATOR, record));
            }
        } catch (IOException e) {
            System.out.println("Error saving lendings.txt: " + e.getMessage());
        }
    }

    // ── Add new lending ───────────────────────────────────────

    public void addLending(String barcode, String username) {
        Date today = new Date();
        long tenDays = 10L * 24 * 60 * 60 * 1000;
        Date dueDate = new Date(today.getTime() + tenDays);

        String[] record = {
                barcode,
                username,
                DATE_FORMAT.format(today),
                DATE_FORMAT.format(dueDate),
                "null" // returnDate is null until returned
        };

        lendingRecords.add(record);
        saveLendings();
        System.out.println("Lending record saved for: " + username);
    }

    // ── Return a book ─────────────────────────────────────────

    public boolean returnLending(String barcode, String username) {
        for (String[] record : lendingRecords) {
            if (record[0].equals(barcode) &&
                    record[1].equals(username) &&
                    record[4].equals("null")) {

                record[4] = DATE_FORMAT.format(new Date());
                saveLendings();
                System.out.println("Return recorded for: " + username);
                return true;
            }
        }
        System.out.println("No active lending found for barcode: " + barcode);
        return false;
    }

    // ── Check if overdue ──────────────────────────────────────

    public boolean isOverdue(String barcode, String username) {
        for (String[] record : lendingRecords) {
            if (record[0].equals(barcode) &&
                    record[1].equals(username) &&
                    record[4].equals("null")) {
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
            if (record[0].equals(barcode) &&
                    record[1].equals(username) &&
                    record[4].equals("null")) {
                try {
                    Date dueDate = DATE_FORMAT.parse(record[3]);
                    Date today = new Date();
                    if (today.after(dueDate)) {
                        long diff = today.getTime() - dueDate.getTime();
                        long daysLate = diff / (1000 * 60 * 60 * 24);
                        return daysLate * 1.0; // $1 per day
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
            if (record[1].equals(username)) {
                result.add(record);
            }
        }
        return result;
    }

    // ── Get active lending for a barcode ──────────────────────

    public String[] getActiveLending(String barcode) {
        for (String[] record : lendingRecords) {
            if (record[0].equals(barcode) && record[4].equals("null")) {
                return record;
            }
        }
        return null;
    }
}