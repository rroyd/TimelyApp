package com.example.ex2.models;

import androidx.annotation.NonNull;

public class Range {
    private Integer startMinute;
    public void setIsSetEnd(boolean setEnd) {
        this.setEnd = setEnd;
    }

    public void setIsSetStart(boolean setStart) {
        this.setStart = setStart;
    }

    public Range(Range range) {
        this.setStart = range.getIsSetStart();
        this.setEnd = range.getIsSetEnd();

        if(setStart) {
            this.startMinute = range.getStartMinute();
            this.startHour = range.getStartHour();
        }
        if(setEnd) {
            this.endHour = range.getEndHour();
            this.endMinute = range.getEndMinute();
        }
    }

    private boolean setEnd;
    private boolean setStart;


    public boolean getIsSetStart() {
        return setStart;
    }
    public boolean getIsSetEnd() {
        return setEnd;
    }



    public void setStartTime(Integer startHour, Integer startMinute) {
        this.startMinute = startMinute;
        this.startHour = startHour;

        setStart = true;
    }

    public void setEndTime(Integer endHour, Integer endMinute) {
        this.endMinute = endMinute;
        this.endHour = endHour;

        setStart = true;
    }

    private Integer endMinute;
    private Integer startHour;
    private Integer endHour;

    public Range(Integer startHour, Integer startMinute, Integer endHour,  Integer endMinute) {
        this.startMinute = startMinute;
        this.endMinute = endMinute;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public Integer getEndHour() {
        return endHour;
    }

    public Integer getStartHour() {
        return startHour;
    }

    public Integer getEndMinute() {
        return endMinute;
    }

    public Integer getStartMinute() {
        return startMinute;
    }

    public Range() {
        setStart = false;
        setEnd = false;
    }

    public Range createRangeFromString(String rangeStr) {
        String[] times = rangeStr.split("[:\\-]");

        return new Range(Integer.parseInt(times[0]), Integer.parseInt(times[1]),
                Integer.parseInt(times[2]), Integer.parseInt(times[3]));
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s:%s-%s:%s", startHour, startMinute, endHour,endMinute);
    }
}
