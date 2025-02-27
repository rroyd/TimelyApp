package com.example.ex2.models;

import androidx.annotation.NonNull;

public class Vacation {
    public String getVacationId() {
        return vacationId;
    }

    public void setVacationId(String vacationId) {
        this.vacationId = vacationId;
    }

    private String vacationId;
    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    private Date from;
    private Date to;

    private String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Vacation() {

    }

    public Vacation(Date from, Date to) {
        this.from = from;
        this.to = to;
    }

    public Vacation(Date from, Date to, String user) {
        this.from = from;
        this.to = to;
        this.user = user;
    }


    @NonNull
    @Override
    public String toString() {
        return String.format(from.showDate() + " - " + to.showDate());
    }
}
