package com.example.beam.models;

import java.util.Map;

public class TimeTable extends Session{
    private String date;

    public TimeTable() {
        super();
    }

    public TimeTable(String date, String moduleID, String sessionID, String sessionType, String timeBegin, String timeEnd, String status) {
        super(moduleID, sessionID, sessionType, timeBegin, timeEnd, status);
        this.date = date;
    }

    @Override
    public String toString() {
        return "TimeTable{" +
                "date='" + date + '\'' +
                "moduleID='" + moduleID + '\'' +
                "sessionID='" + sessionID + '\'' +
                "sessionType='" + sessionType + '\'' +
                "timeBegin='" + timeBegin + '\'' +
                "timeEnd='" + timeEnd + '\'' +
                ", status='" + status +
                '}';
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
