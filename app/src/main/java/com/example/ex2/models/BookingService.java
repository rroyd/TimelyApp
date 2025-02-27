package com.example.ex2.models;

import java.io.Serializable;

public class BookingService implements Serializable {
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    private String customerId;
    private Service service;
    private Date when;

    public String getBookingServiceId() {
        return bookingServiceId;
    }

    public void setBookingServiceId(String bookingServiceId) {
        this.bookingServiceId = bookingServiceId;
    }

    private String bookingServiceId;

    public BookingService() {
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Date getWhen() {
        return when;
    }

    public void setWhen(Date when) {
        this.when = when;
    }

    public BookingService(Service service, Date when) {
        this.service = service;
        this.when = when;
    }
}
