package org.example;

import java.util.Date;

public class BookReservation {
    private Date creationDate;
    private ReservationStatus status;

    public BookReservation(ReservationStatus status) {
        this.creationDate = new Date();
        this.status = status;
    }


    public Date getCreationDate() {
        return creationDate;
    }


    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
    public BookReservation fetchReservationDetails() {
        return this;
    }
}
