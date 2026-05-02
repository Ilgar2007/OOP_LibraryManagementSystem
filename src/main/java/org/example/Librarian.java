package org.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Librarian extends Account {

    private Catalog catalog;  // ← add this field

    public Librarian(String id, String password,
                     AccountStatus status, Person person, Catalog catalog) {
        super(id, password, status, person);
        this.catalog = catalog;
    }

    public boolean addBookItem() {
        BookItem bookItem = new BookItem(
                "12",
                "Harry Potter",
                "Fantasy",
                "Bloomsbury",
                "English",
                887,
                new ArrayList<>(),
                "123",
                true,
                123.56,
                BookFormat.HARDCOVER,
                BookStatus.AVAILABLE,
                new Date(2202, 10, 10),
                new Date(1998, 1, 13)
        );

        Scanner scan = new Scanner(System.in);

        System.out.println("Input the book item barcode: ");
        String barcode = scan.nextLine();
        bookItem.setBarcode(barcode);

        System.out.println("Input the book item price: ");
        double price = scan.nextDouble();
        bookItem.setPrice(price);

        System.out.println("Is it reference only? (true/false): ");
        boolean isReferenceOnly = scan.nextBoolean();
        bookItem.setReferenceOnly(isReferenceOnly);

        System.out.println("Input format (HARDCOVER, PAPERBACK, EBOOK, AUDIOBOOK): ");
        String formatStr = scan.next();
        bookItem.setFormat(BookFormat.valueOf(formatStr.toUpperCase()));

        catalog.updateCatalog(bookItem);

        System.out.println("Book item added successfully!");
        return true;
    }

    public boolean addBook(Book book) {
        if (book == null) {
            System.out.println("Book not found!");
            return false;
        }
        System.out.println("Book added: " + book.getTitle());
        return true;
    }

    public boolean removeBook(Book book) {
        if (book == null) {
            System.out.println("Book not found!");
            return false;
        }
        catalog.getBookTitles().remove(book.getTitle());
        catalog.getBookSubjects().remove(book.getSubject());
        System.out.println("Book removed: " + book.getTitle());
        return true;
    }


    public boolean removeBookItem(BookItem bookItem) {
        if (bookItem == null) {
            System.out.println("Book item not found!");
            return false;
        }
        bookItem.setStatus(BookStatus.LOST);
        System.out.println("Book item removed: " + bookItem.getBarcode());
        return true;
    }

    public boolean editBookItem(BookItem bookItem) {
        if (bookItem == null) {
            System.out.println("Book item not found!");
            return false;
        }
        catalog.updateCatalog(bookItem);
        System.out.println("Book item updated: " + bookItem.getBarcode());
        return true;
    }

    public boolean issueLibraryCard(Member member) {
        if (member == null) {
            System.out.println("Member not found!");
            return false;
        }
        LibraryCard card = new LibraryCard(
                "CARD-" + member.getId(),
                "BARCODE-" + member.getId()
        );
        member.setLibraryCard(card);
        System.out.println("Library card issued to: " + member.getId());
        return true;
    }

    public boolean issueBook(BookItem bookItem, Member member) {
        if (bookItem == null || member == null) {
            System.out.println("Book or member not found!");
            return false;
        }
        if (bookItem.getStatus() != BookStatus.AVAILABLE) {
            System.out.println("Book is not available!");
            return false;
        }
        bookItem.setStatus(BookStatus.LOANED);
        bookItem.setBorrowed(new Date());
        System.out.println("Book issued to: " + member.getId());
        return true;
    }

    public boolean blockMember(Member member) {
        if (member == null) {
            System.out.println("Member not found!");
            return false;
        }
        member.setStatus(AccountStatus.BLACKLISTED);
        System.out.println("Member blocked!");
        return true;
    }

    public boolean unblockMember(Member member) {
        if (member == null) {
            System.out.println("Member not found");
            return false;
        }
        member.setStatus(AccountStatus.ACTIVE);
        System.out.println("Member unblocked");
        return true;
    }
}