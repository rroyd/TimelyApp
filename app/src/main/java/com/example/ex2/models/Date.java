package com.example.ex2.models;

import androidx.annotation.Nullable;

import java.time.Year;
import java.util.Calendar;

public class Date {
    private int year;
    private int month;
    private int day;
    private int hour;

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    private int minute;

    public Date() {

    }

    public Date(int year, int month, int day, int hour, int minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public long getTimestampFromDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month, day, hour, minute, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    @Override
    public String toString() {
        return String.format("בתאריך %d/%02d/%02d בשעה %02d:%02d", year, month + 1, day, hour, minute);
    }

    public String showDate() {
        return String.format("%d/%02d/%02d", year, month + 1, day);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Date date = (Date) obj;

        return year == date.getYear()
                &&  month == date.getMonth()
                && day == date.getDay()
                && hour == date.getHour()
                && minute == date.getMinute();
    }

    public String displayHourAndMinute() {
        return String.format("%02d:%02d", hour, minute);
    }

}
