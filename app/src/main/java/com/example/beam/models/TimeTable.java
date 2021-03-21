package com.example.beam.models;

import java.util.List;
import java.util.Map;

public class TimeTable{
    // Key: Date, Value: Sessions of the day
    private Map<String, List<Session>> weeklyTimetable;

    public TimeTable() {

    }

    public TimeTable(Map<String, List<Session>> weeklyTimetable) {
        this.weeklyTimetable = weeklyTimetable;
    }

    public void setWeeklyTimetable(Map<String, List<Session>> weeklyTimetable) {
        this.weeklyTimetable = weeklyTimetable;
    }

    public Map<String, List<Session>> getWeeklyTimetable() {
        return weeklyTimetable;
    }

    public void putDailyTimetable(String date, List<Session> sessions) {
        weeklyTimetable.put(date, sessions);
    }

    public List<Session> getDailyTimetable(String date) throws NullPointerException {
        try {
            return weeklyTimetable.get(date);
        }
        catch (NullPointerException exception) {
            throw exception;
        }
    }
}
