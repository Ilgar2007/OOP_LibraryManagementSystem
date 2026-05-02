package org.example;

import java.util.Date;

public class Member extends Account {
    private Date dateOfMembership;
    private int totalBooksCheckedOut;
    private LibraryCard libraryCard;

    public Member(String id, String password, AccountStatus status,
                  Person person, Date dateOfMembership) {
        super(id, password, status, person);
        this.dateOfMembership = dateOfMembership;
        this.totalBooksCheckedOut = 0;
        this.libraryCard = null;
    }

    public int getTotalBooksCheckedOut() {
        return totalBooksCheckedOut;
    }

    public Date getDateOfMembership() {
        return dateOfMembership;
    }

    public void setDateOfMembership(Date dateOfMembership) {
        this.dateOfMembership = dateOfMembership;
    }

    public void setTotalBooksCheckedOut(int totalBooksCheckedOut) {
        this.totalBooksCheckedOut = totalBooksCheckedOut;
    }

    public LibraryCard getLibraryCard() { return libraryCard; }
    public void setLibraryCard(LibraryCard libraryCard) { this.libraryCard = libraryCard; }

    public boolean checkoutBook(BookItem book) {
        if (book == null) {
            System.out.println("Book not found!");
            return false;
        }
        if (book.isReferenceOnly()) {
            System.out.println("This book is reference only — cannot checkout!");
            return false;
        }
        if (totalBooksCheckedOut >= 5) {
            System.out.println("Cannot checkout more than 5 books!");
            return false;
        }
        if (book.getStatus() == BookStatus.RESERVED) {
            System.out.println("This book is reserved by another member!");
            return false;
        }
        book.setStatus(BookStatus.LOANED);
        book.setBorrowed(new Date());
        totalBooksCheckedOut++;
        System.out.println("Book checked out successfully: " + book.getTitle());
        return true;
    }


    public boolean renewBook(BookItem book) {
        if (book == null) {
            System.out.println("Book not found!");
            return false;
        }

        // check if overdue
        Date today = new Date();
        if (book.getDueDate() != null && today.after(book.getDueDate())) {
            long diff = today.getTime() - book.getDueDate().getTime();
            long daysLate = diff / (1000 * 60 * 60 * 24);
            double fineAmount = daysLate * 1.0;
            Fine fine = new Fine(fineAmount);
            System.out.println("Overdue fine before renewal: $" + fine.getAmount());
        }

        // check if reserved by another member
        if (book.getStatus() == BookStatus.RESERVED) {
            System.out.println("Cannot renew — book is reserved by another member!");
            book.setStatus(BookStatus.RESERVED);
            return false;
        }

        // extend due date by 10 days
        long tenDays = 10L * 24 * 60 * 60 * 1000;
        Date newDueDate = new Date(today.getTime() + tenDays);
        book.setDueDate(newDueDate);
        System.out.println("Book renewed! New due date: " + newDueDate);
        return true;
    }

    public boolean reserveBook(BookItem book) {
        if (book == null){
            System.out.println("Book not found");
            return false;
        }

        if (book.getStatus() != BookStatus.AVAILABLE){
            System.out.println("Book is not available");
        }

        book.setStatus(BookStatus.RESERVED);
        System.out.println("Book reserved: " + book.getTitle());
        return true;
    }


    public boolean returnBook(BookItem book) {
        if (book == null) {
            System.out.println("Book not found!");
            return false;
        }

        // check if overdue
        Date today = new Date();
        if (book.getDueDate() != null && today.after(book.getDueDate())) {
            long diff = today.getTime() - book.getDueDate().getTime();
            long daysLate = diff / (1000 * 60 * 60 * 24);
            double fineAmount = daysLate * 1.0; // $1 per day
            Fine fine = new Fine(fineAmount);
            System.out.println("Book overdue! Fine: $" + fine.getAmount());
        }

        totalBooksCheckedOut--;

        // check if reserved by someone else
        if (book.getStatus() == BookStatus.RESERVED) {
            book.setStatus(BookStatus.RESERVED);
            System.out.println("Book returned and marked reserved for next member.");
        } else {
            book.setStatus(BookStatus.AVAILABLE);
            System.out.println("Book returned successfully: " + book.getTitle());
        }
        return true;
    }

    public boolean payFine(Fine fine) {
        if (fine == null){
            System.out.println("Fine doesnt exist");
            return false;
        }
        System.out.println("Paid amound: " + fine.getAmount());
        return true;
    }


    public boolean removeReservation(BookReservation reservation) {
        if (reservation == null) {
            System.out.println("Reservation not found!");
            return false;
        }
        reservation.setStatus(ReservationStatus.CANCELED);
        System.out.println("Reservation removed!");
        return true;
    }
}
