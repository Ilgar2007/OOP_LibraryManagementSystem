package org.example;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class ReservationManager {

    private static ReservationManager instance;
    private static final String FILE_NAME = "reservations.txt";
    private static final String SEPARATOR = "|";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private List<String[]> reservationRecords;

    private ReservationManager() {
        this.reservationRecords = new ArrayList<>();
        loadReservations();
    }

    public static ReservationManager getInstance() {
        if (instance == null) {
            instance = new ReservationManager();
        }
        return instance;
    }

    // ── Load from file ────────────────────────────────────────

    private void loadReservations() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length < 4) continue;
                reservationRecords.add(parts);
            }
        } catch (IOException e) {
            System.out.println("Error reading reservations.txt: " + e.getMessage());
        }
    }

    // ── Save to file ──────────────────────────────────────────

    private void saveReservations() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME, false))) {
            for (String[] record : reservationRecords) {
                pw.println(String.join(SEPARATOR, record));
            }
        } catch (IOException e) {
            System.out.println("Error saving reservations.txt: " + e.getMessage());
        }
    }

    // ── Add new reservation ───────────────────────────────────

    public void addReservation(String barcode, String username) {
        String[] record = {
                barcode,
                username,
                DATE_FORMAT.format(new Date()),
                ReservationStatus.WAITING.toString()
        };
        reservationRecords.add(record);
        saveReservations();
        System.out.println("Reservation saved for: " + username);
    }

    // ── Cancel reservation ────────────────────────────────────

    public boolean cancelReservation(String barcode, String username) {
        for (String[] record : reservationRecords) {
            if (record[0].equals(barcode) &&
                    record[1].equals(username) &&
                    record[3].equals(ReservationStatus.WAITING.toString())) {

                record[3] = ReservationStatus.CANCELED.toString();
                saveReservations();
                System.out.println("Reservation canceled for: " + username);
                return true;
            }
        }
        System.out.println("No active reservation found.");
        return false;
    }

    // ── Complete reservation ──────────────────────────────────

    public boolean completeReservation(String barcode, String username) {
        for (String[] record : reservationRecords) {
            if (record[0].equals(barcode) &&
                    record[1].equals(username) &&
                    record[3].equals(ReservationStatus.WAITING.toString())) {

                record[3] = ReservationStatus.COMPLETED.toString();
                saveReservations();
                System.out.println("Reservation completed for: " + username);
                return true;
            }
        }
        return false;
    }

    // ── Check if book has active reservation ──────────────────

    public boolean hasActiveReservation(String barcode) {
        for (String[] record : reservationRecords) {
            if (record[0].equals(barcode) &&
                    record[3].equals(ReservationStatus.WAITING.toString())) {
                return true;
            }
        }
        return false;
    }

    // ── Get all reservations for a user ───────────────────────

    public List<String[]> getReservationsForUser(String username) {
        List<String[]> result = new ArrayList<>();
        for (String[] record : reservationRecords) {
            if (record[1].equals(username)) {
                result.add(record);
            }
        }
        return result;
    }
}