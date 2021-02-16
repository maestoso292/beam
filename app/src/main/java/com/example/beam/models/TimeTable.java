package com.example.beam.models;

import java.util.Map;

public class TimeTable extends Session{
    private String date;

    public TimeTable() {
        super();
    }

    public TimeTable(String date, String moduleID, String sessionID, String sessionType, String timeBegin, String timeEnd, String status) {
        super(moduleID, sessionID, sessionType, timeBegin, timeEnd, status)
        this.date = date;
    }
}
