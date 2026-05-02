package org.example;

public class CheckTransaction extends FineTransaction {
    private String bankName;
    private String checkNumber;

    public CheckTransaction(double amount, String bankName, String checkNumber) {
        super(amount);
        this.bankName = bankName;
        this.checkNumber = checkNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    @Override
    public boolean initiateTransaction() {
        System.out.println("Processing check from: " + bankName);
        return true;
    }
}
