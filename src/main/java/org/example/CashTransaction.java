package org.example;

public class CashTransaction extends FineTransaction {
    private double cashTendered;

    public CashTransaction(double amount, double cashTendered) {
        super(amount);
        this.cashTendered = cashTendered;
    }

    @Override
    public boolean initiateTransaction() {
        System.out.println("Processing cash transaction: " + cashTendered);
        return true;
    }

    public double getCashTendered() {
        return cashTendered;
    }

    public void setCashTendered(double cashTendered) {
        this.cashTendered = cashTendered;
    }
}
