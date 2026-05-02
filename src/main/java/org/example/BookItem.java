package org.example;

import java.util.Date;
import java.util.List;

public class BookItem extends Book{
        private String barcode;
        private boolean isReferenceOnly;
        private Date borrowed;
        private Date dueDate;
        private double price;
        private BookFormat format;
        private BookStatus status;
        private Date dateOfPurchase;
        private Date publicationDate;

        public BookItem(String ISBN, String title, String subject, String publisher,
                        String language, int numberOfPages, List<Author> authors,
                        String barcode, boolean isReferenceOnly, double price,
                        BookFormat format, BookStatus status, Date dateOfPurchase,
                        Date publicationDate) {
                super(ISBN, title, subject, publisher, language, numberOfPages, authors);
                this.barcode = barcode;
                this.isReferenceOnly = isReferenceOnly;
                this.price = price;
                this.format = format;
                this.status = status;
                this.dateOfPurchase = dateOfPurchase;
                this.publicationDate = publicationDate;
        }


        public String getBarcode() {
                return barcode;
        }

        public void setBarcode(String barcode) {
                this.barcode = barcode;
        }

        public boolean isReferenceOnly() {
                return isReferenceOnly;
        }

        public void setReferenceOnly(boolean referenceOnly) {
                isReferenceOnly = referenceOnly;
        }

        public Date getBorrowed() {
                return borrowed;
        }

        public void setBorrowed(Date borrowed) {
                this.borrowed = borrowed;
        }

        public Date getDueDate() {
                return dueDate;
        }

        public void setDueDate(Date dueDate) {
                this.dueDate = dueDate;
        }

        public double getPrice() {
                return price;
        }

        public void setPrice(double price) {
                this.price = price;
        }

        public BookFormat getFormat() {
                return format;
        }

        public void setFormat(BookFormat format) {
                this.format = format;
        }

        public BookStatus getStatus() {
                return status;
        }

        public void setStatus(BookStatus status) {
                this.status = status;
        }

        public Date getDateOfPurchase() {
                return dateOfPurchase;
        }

        public void setDateOfPurchase(Date dateOfPurchase) {
                this.dateOfPurchase = dateOfPurchase;
        }

        public Date getPublicationDate() {
                return publicationDate;
        }

        public void setPublicationDate(Date publicationDate) {
                this.publicationDate = publicationDate;
        }
}
