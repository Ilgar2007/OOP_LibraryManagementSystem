package org.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Catalog implements Search {
    private Date creationDate;
    private int totalBooks;
    private Map<String, List<BookItem>> bookTitles;
    private Map<String, List<BookItem>> bookAuthors;
    private Map<String, List<BookItem>> bookSubjects;
    private Map<Date, List<BookItem>> bookPublicationDates;

    public Catalog() {
        this.creationDate = new Date();
        this.totalBooks = 0;
        this.bookTitles = new HashMap<>();
        this.bookAuthors = new HashMap<>();
        this.bookSubjects = new HashMap<>();
        this.bookPublicationDates = new HashMap<>();
    }

    // ── Search interface methods ──────────────────────────────

    @Override
    public String searchByTitle(String title) {
        List<BookItem> books = bookTitles.get(title);
        if (books == null || books.isEmpty()) {
            return "No books found with title: " + title;
        }
        StringBuilder result = new StringBuilder();
        for (BookItem book : books) {
            result.append("Title: ").append(book.getTitle()).append("\n")
                    .append("ISBN: ").append(book.getISBN()).append("\n")
                    .append("Barcode: ").append(book.getBarcode()).append("\n")
                    .append("Status: ").append(book.getStatus()).append("\n")
                    .append("Format: ").append(book.getFormat()).append("\n")
                    .append("Price: $").append(book.getPrice()).append("\n")
                    .append("─────────────────\n");
        }
        return result.toString();
    }

    @Override
    public String searchByAuthor(String authorName) {
        List<BookItem> books = bookAuthors.get(authorName);
        if (books == null || books.isEmpty()) {
            return "No books found by author: " + authorName;
        }
        StringBuilder result = new StringBuilder();
        for (BookItem book : books) {
            result.append("Title: ").append(book.getTitle())
                    .append(", Author: ").append(authorName)
                    .append("\n");
        }
        return result.toString();
    }

    @Override
    public String searchBySubject(String subject) {
        List<BookItem> books = bookSubjects.get(subject);
        if (books == null || books.isEmpty()) {
            return "No books found with subject: " + subject;
        }
        StringBuilder result = new StringBuilder();
        for (BookItem book : books) {
            result.append("Title: ").append(book.getTitle())
                    .append(", Subject: ").append(book.getSubject())
                    .append("\n");
        }
        return result.toString();
    }

    @Override
    public Date searchByPubDate(Date pubDate) {
        List<BookItem> books = bookPublicationDates.get(pubDate);
        if (books == null || books.isEmpty()) {
            return null;
        }
        return pubDate;
    }

    // ── updateCatalog ─────────────────────────────────────────

    public boolean updateCatalog(BookItem bookItem) {
        try {
            bookTitles
                    .computeIfAbsent(bookItem.getTitle(), k -> new ArrayList<>())
                    .add(bookItem);

            for (Author author : bookItem.getAuthors()) {
                bookAuthors
                        .computeIfAbsent(author.getName(), k -> new ArrayList<>())
                        .add(bookItem);
            }

            bookSubjects
                    .computeIfAbsent(bookItem.getSubject(), k -> new ArrayList<>())
                    .add(bookItem);

            bookPublicationDates
                    .computeIfAbsent(bookItem.getPublicationDate(), k -> new ArrayList<>())
                    .add(bookItem);

            totalBooks++;
            return true;

        } catch (Exception e) {
            System.out.println("Failed to update catalog: " + e.getMessage());
            return false;
        }
    }

    // ── Getters and Setters ───────────────────────────────────

    public Date getCreationDate() { return creationDate; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    public int getTotalBooks() { return totalBooks; }
    public void setTotalBooks(int totalBooks) { this.totalBooks = totalBooks; }

    public Map<String, List<BookItem>> getBookTitles() { return bookTitles; }
    public void setBookTitles(Map<String, List<BookItem>> bookTitles) { this.bookTitles = bookTitles; }

    public Map<String, List<BookItem>> getBookAuthors() { return bookAuthors; }
    public void setBookAuthors(Map<String, List<BookItem>> bookAuthors) { this.bookAuthors = bookAuthors; }

    public Map<String, List<BookItem>> getBookSubjects() { return bookSubjects; }
    public void setBookSubjects(Map<String, List<BookItem>> bookSubjects) { this.bookSubjects = bookSubjects; }

    public Map<Date, List<BookItem>> getBookPublicationDates() { return bookPublicationDates; }
    public void setBookPublicationDates(Map<Date, List<BookItem>> bookPublicationDates) { this.bookPublicationDates = bookPublicationDates; }
}