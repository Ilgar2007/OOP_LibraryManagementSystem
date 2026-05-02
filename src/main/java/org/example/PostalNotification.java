package org.example;

public class PostalNotification extends Notification{
    private Address address;

    public PostalNotification(int notificationId, String content, Address Address){
        super(notificationId, content);
        this.address = Address;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }


    public boolean sendNotification() {
        System.out.println("Sending postal notification to: " + address);
        return true;
    }

}
