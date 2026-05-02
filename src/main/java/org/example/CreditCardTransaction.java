package org.example;

public class CreditCardTransaction extends FineTransaction {
    private String nameOnCard;

    public CreditCardTransaction(double amount, String nameOnCard) {
        super(amount);
        this.nameOnCard = nameOnCard;
    }


    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }


    @Override
    public boolean initiateTransaction() {
        System.out.println("Processing credit card transaction for: " + nameOnCard);
        return true;
    }
}
