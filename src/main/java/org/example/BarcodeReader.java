package org.example;

import java.util.Date;

public class BarcodeReader {
    private String id;
    private Date registeredAt;
    private boolean active;

    public BarcodeReader(String id) {
        this.id = id;
        this.registeredAt = new Date();
        this.active = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getRegisteredAt() {
        return registeredAt;
    }

    public boolean isActive() {
        return active;
    }
}
