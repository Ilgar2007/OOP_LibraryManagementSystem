package org.example;

import java.util.Date;

public abstract class Notification {
    private int notificationId;
    private Date createdOn;
    private String content;

    public Notification(int notificationId, String content) {
        this.notificationId = notificationId;
        this.createdOn = new Date();
        this.content = content;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean sendNotification() {
        if (content == null || content.isEmpty()) {
            System.out.println("No content to send!");
            return false;
        }

        System.out.println("Sending: " + content);
        return true;
    }

    public boolean sendOverdueNotification() {
        this.content = "Your book is overdue. Please return it as soon as possible. ";
        return sendNotification();
    }

    public boolean sendReservationAvailableNotification() {
        this.content = "The book you reserved is now available for pickup.";
        return sendNotification();
    }

    public boolean sendReservationCanceledNotification() {
        this.content = "Your reservation has been canceled.";
        return sendNotification();
    }
}
