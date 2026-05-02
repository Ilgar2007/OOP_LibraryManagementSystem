package org.example;

import java.util.Date;

public abstract class FineTransaction {
    private Date creationDate;
    private double amount;

    public FineTransaction(double amount) {
        this.creationDate = new Date();
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public abstract boolean initiateTransaction();
}