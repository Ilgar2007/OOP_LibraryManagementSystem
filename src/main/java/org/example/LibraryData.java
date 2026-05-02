package org.example;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class LibraryData {

    // ── JDBC setup
    // SQLite creates "library.db" in the project root automatically.
    private static final String DB_URL = "jdbc:sqlite:library.db";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // ── Singleton
    private static LibraryData instance;

    private Catalog catalog;
    private List<BookItem> books;
    private List<Member>   members;

    private LibraryData() {
        this.catalog = new Catalog();
        this.books   = new ArrayList<>();
        this.members = new ArrayList<>();

        initDatabase();   // create tables if they don't exist
        loadBooks();      // read rows → in-memory list
        loadMembers();

        if (books.isEmpty()) {
            loadSampleData();
        }
    }

    public static LibraryData getInstance() {
        if (instance == null) {
            instance = new LibraryData();
        }
        return instance;
    }


    private void initDatabase() {
        String createBooks = """
            CREATE TABLE IF NOT EXISTS books (
                barcode       TEXT PRIMARY KEY,
                isbn          TEXT NOT NULL,
                title         TEXT NOT NULL,
                subject       TEXT,
                publisher     TEXT,
                language      TEXT,
                pages         INTEGER,
                price         REAL,
                format        TEXT,
                status        TEXT,
                ref_only      INTEGER,
                purchase_date TEXT,
                pub_date      TEXT,
                authors       TEXT
            )""";

        String createMembers = """
            CREATE TABLE IF NOT EXISTS members (
                username          TEXT PRIMARY KEY,
                date_of_membership TEXT,
                total_checked_out INTEGER,
                status            TEXT
            )""";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createBooks);
            stmt.execute(createMembers);
            System.out.println("Database ready.");
        } catch (SQLException e) {
            System.out.println("DB init error: " + e.getMessage());
        }
    }

    /** Opens a new connection to the SQLite database file. */
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }


    // LOAD FROM DATABASE


    private void loadBooks() {
        String sql = "SELECT * FROM books";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                BookItem book = resultSetToBook(rs);
                if (book != null) {
                    books.add(book);
                    catalog.updateCatalog(book);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error loading books: " + e.getMessage());
        }
    }

    private void loadMembers() {
        String sql = "SELECT * FROM members";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Member member = resultSetToMember(rs);
                if (member != null) members.add(member);
            }
        } catch (SQLException e) {
            System.out.println("Error loading members: " + e.getMessage());
        }
    }


    // SAVE / UPDATE


    /** Inserts or replaces a book row (upsert via INSERT OR REPLACE). */
    private void saveBookToDB(BookItem book) {
        String sql = """
            INSERT OR REPLACE INTO books
            (barcode, isbn, title, subject, publisher, language, pages, price,
             format, status, ref_only, purchase_date, pub_date, authors)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1,  book.getBarcode());
            ps.setString(2,  book.getISBN());
            ps.setString(3,  book.getTitle());
            ps.setString(4,  book.getSubject());
            ps.setString(5,  book.getPublisher());
            ps.setString(6,  book.getLanguage());
            ps.setInt   (7,  book.getNumberOfPages());
            ps.setDouble(8,  book.getPrice());
            ps.setString(9,  book.getFormat().toString());
            ps.setString(10, book.getStatus().toString());
            ps.setInt   (11, book.isReferenceOnly() ? 1 : 0);
            ps.setString(12, DATE_FORMAT.format(book.getDateOfPurchase()));
            ps.setString(13, DATE_FORMAT.format(book.getPublicationDate()));
            ps.setString(14, authorsToString(book));

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving book: " + e.getMessage());
        }
    }


    private void saveMemberToDB(Member member) {
        String sql = """
            INSERT OR REPLACE INTO members
            (username, date_of_membership, total_checked_out, status)
            VALUES (?, ?, ?, ?)""";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, member.getId());
            ps.setString(2, DATE_FORMAT.format(member.getDateOfMembership()));
            ps.setInt   (3, member.getTotalBooksCheckedOut());
            ps.setString(4, member.getStatus().toString());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving member: " + e.getMessage());
        }
    }


    // PUBLIC API


    public void addBook(BookItem book) {
        books.add(book);
        catalog.updateCatalog(book);
        saveBookToDB(book);          // ← was: saveBooks() to txt
    }

    public void updateBook(BookItem book) {
        saveBookToDB(book);          // ← was: saveBooks() to txt
    }

    public void addMember(Member member) {
        members.add(member);
        saveMemberToDB(member);      // ← was: saveMembers() to txt
    }

    public void updateMember(Member member) {
        saveMemberToDB(member);      // ← was: saveMembers() to txt
    }

    public BookItem findBookByBarcode(String barcode) {
        for (BookItem book : books) {
            if (book.getBarcode().equals(barcode)) return book;
        }
        return null;
    }

    public BookItem findBookByTitle(String title) {
        for (BookItem book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) return book;
        }
        return null;
    }

    public Member findMemberByUsername(String username) {
        for (Member member : members) {
            if (member.getId().equalsIgnoreCase(username)) return member;
        }
        return null;
    }

    public List<BookItem> getAllBooks() { return books; }
    public Catalog        getCatalog() { return catalog; }
    public List<Member>   getMembers() { return members; }


    // SAMPLE DATA


    private void loadSampleData() {
        List<Author> a1 = new ArrayList<>();
        a1.add(new Author("J.K. Rowling", "British author"));
        addBook(new BookItem("978-0-7432-7356-5", "Harry Potter", "Fantasy",
                "Bloomsbury", "English", 500, a1, "BC001", false, 19.99,
                BookFormat.HARDCOVER, BookStatus.AVAILABLE, new Date(), new Date()));

        List<Author> a2 = new ArrayList<>();
        a2.add(new Author("George Orwell", "English novelist"));
        addBook(new BookItem("978-0-452-28423-4", "1984", "Dystopian",
                "Secker and Warburg", "English", 328, a2, "BC002", false, 14.99,
                BookFormat.PAPERBACK, BookStatus.AVAILABLE, new Date(), new Date()));

        List<Author> a3 = new ArrayList<>();
        a3.add(new Author("F. Scott Fitzgerald", "American novelist"));
        addBook(new BookItem("978-0-7432-7357-2", "The Great Gatsby", "Classic",
                "Scribner", "English", 180, a3, "BC003", false, 12.99,
                BookFormat.PAPERBACK, BookStatus.AVAILABLE, new Date(), new Date()));
    }


    // PRIVATE HELPERS


    private BookItem resultSetToBook(ResultSet rs) {
        try {
            String barcode    = rs.getString("barcode");
            String isbn       = rs.getString("isbn");
            String title      = rs.getString("title");
            String subject    = rs.getString("subject");
            String publisher  = rs.getString("publisher");
            String language   = rs.getString("language");
            int    pages      = rs.getInt("pages");
            double price      = rs.getDouble("price");
            BookFormat format = BookFormat.valueOf(rs.getString("format"));
            BookStatus status = BookStatus.valueOf(rs.getString("status"));
            boolean refOnly   = rs.getInt("ref_only") == 1;
            Date purchaseDate = DATE_FORMAT.parse(rs.getString("purchase_date"));
            Date pubDate      = DATE_FORMAT.parse(rs.getString("pub_date"));

            List<Author> authors = new ArrayList<>();
            String authorsStr = rs.getString("authors");
            if (authorsStr != null && !authorsStr.isBlank()) {
                for (String name : authorsStr.split(",")) {
                    authors.add(new Author(name.trim(), ""));
                }
            }

            BookItem book = new BookItem(isbn, title, subject, publisher, language,
                    pages, authors, barcode, refOnly, price, format, status,
                    purchaseDate, pubDate);
            return book;

        } catch (SQLException | ParseException | IllegalArgumentException e) {
            System.out.println("Skipping invalid book row: " + e.getMessage());
            return null;
        }
    }

    private Member resultSetToMember(ResultSet rs) {
        try {
            String username        = rs.getString("username");
            Date dateOfMembership  = DATE_FORMAT.parse(rs.getString("date_of_membership"));
            int totalCheckedOut    = rs.getInt("total_checked_out");
            AccountStatus status   = AccountStatus.valueOf(rs.getString("status"));

            Person person = new Person(username, "", "", null);
            Member member = new Member(username, "", status, person, dateOfMembership);
            member.setTotalBooksCheckedOut(totalCheckedOut);
            return member;

        } catch (SQLException | ParseException | IllegalArgumentException e) {
            System.out.println("Skipping invalid member row: " + e.getMessage());
            return null;
        }
    }

    private String authorsToString(BookItem book) {
        StringBuilder sb = new StringBuilder();
        List<Author> authors = book.getAuthors();
        for (int i = 0; i < authors.size(); i++) {
            sb.append(authors.get(i).getName());
            if (i < authors.size() - 1) sb.append(",");
        }
        return sb.toString();
    }
}
