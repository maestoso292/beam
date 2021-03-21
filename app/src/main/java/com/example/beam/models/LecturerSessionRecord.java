package com.example.beam.models;

import java.util.Map;

public class LecturerSessionRecord {
    private String sessionID;
    private Map<String, Boolean> sessionAttendance;

    public LecturerSessionRecord() {

    }

    public LecturerSessionRecord(String sessionID, Map<String, Boolean> attendance) {
        this.sessionID = sessionID;
        this.sessionAttendance = attendance;
    }


    @Override
    public String toString() {
        return "LecturerSessionRecord{" +
                "sessionID='" + sessionID + '\'' +
                ", attendance=" + sessionAttendance +
                '}';
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public Map<String, Boolean> getSessionAttendance() {
        return sessionAttendance;
    }

    public void setSessionAttendance(Map<String, Boolean> sessionAttendance) {
        this.sessionAttendance = sessionAttendance;
    }


}
