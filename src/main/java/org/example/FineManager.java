package org.example;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class FineManager {

    private static FineManager instance;
    private static final String FILE_NAME = "fines.txt";
    private static final String SEPARATOR = "|";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private List<String[]> fineRecords;

    private FineManager() {
        this.fineRecords = new ArrayList<>();
        loadFines();
    }

    public static FineManager getInstance() {
        if (instance == null) {
            instance = new FineManager();
        }
        return instance;
    }

    // ── Load from file ────────────────────────────────────────

    private void loadFines() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length < 5) continue;
                fineRecords.add(parts);
            }
        } catch (IOException e) {
            System.out.println("Error reading fines.txt: " + e.getMessage());
        }
    }

    // ── Save to file ──────────────────────────────────────────

    private void saveFines() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME, false))) {
            for (String[] record : fineRecords) {
                pw.println(String.join(SEPARATOR, record));
            }
        } catch (IOException e) {
            System.out.println("Error saving fines.txt: " + e.getMessage());
        }
    }

    // ── Add new fine ──────────────────────────────────────────

    public void addFine(String username, String barcode, double amount) {
        String[] record = {
                username,
                barcode,
                String.valueOf(amount),
                "false", // not paid yet
                DATE_FORMAT.format(new Date())
        };
        fineRecords.add(record);
        saveFines();
        System.out.println("Fine of $" + amount + " added for: " + username);
    }

    // ── Pay fine ──────────────────────────────────────────────

    public boolean payFine(String username, String barcode) {
        for (String[] record : fineRecords) {
            if (record[0].equals(username) &&
                    record[1].equals(barcode) &&
                    record[3].equals("false")) {

                record[3] = "true";
                saveFines();
                System.out.println("Fine paid for: " + username);
                return true;
            }
        }
        System.out.println("No unpaid fine found.");
        return false;
    }

    // ── Get unpaid fines for a user ───────────────────────────

    public List<String[]> getUnpaidFines(String username) {
        List<String[]> result = new ArrayList<>();
        for (String[] record : fineRecords) {
            if (record[0].equals(username) && record[3].equals("false")) {
                result.add(record);
            }
        }
        return result;
    }

    // ── Get total unpaid amount for a user ────────────────────

    public double getTotalUnpaidAmount(String username) {
        double total = 0.0;
        for (String[] record : fineRecords) {
            if (record[0].equals(username) && record[3].equals("false")) {
                total += Double.parseDouble(record[2]);
            }
        }
        return total;
    }

    // ── Check if user has unpaid fines ────────────────────────

    public boolean hasUnpaidFines(String username) {
        for (String[] record : fineRecords) {
            if (record[0].equals(username) && record[3].equals("false")) {
                return true;
            }
        }
        return false;
    }
}