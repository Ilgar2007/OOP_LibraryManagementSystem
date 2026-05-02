package org.example;

import java.util.Date;

public class LibraryCard {
    private String cardNumber;
    private String barcode;
    private Date issuedAt;
    private boolean active;

    public LibraryCard(String cardNumber, String barcode) {
        this.cardNumber = cardNumber;
        this.barcode = barcode;
        this.issuedAt = new Date();
        this.active = true;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public boolean isActive() { return active; }
}
