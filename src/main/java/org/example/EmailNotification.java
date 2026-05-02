package org.example;

public class EmailNotification extends Notification {
    private String email;

    public EmailNotification(int notificationId, String content, String email) {
        super(notificationId, content);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean sendNotification() {
        System.out.println("Sending email notification to: " + email);
        return true;
    }
}