package com.example.beam.models;

import java.util.Map;

public class Record {
    private String moduleID;
    private String sessionID;
    private Map<String, String> studentsAttended;

    public Record() {}

    public Record(String moduleID, String sessionID, Map<String, String> studentsAttended) {
        this.moduleID = moduleID;
        this.sessionID = sessionID;
        this.studentsAttended = studentsAttended;
    }

    @Override
    public String toString() {
        return "Record{" +
                "moduleID='" + moduleID + '\'' +
                "sessionID='" + sessionID + '\'' +
                ", studentsAttended='" + studentsAttended +
                '}';
    }

    public String getModuleID() {
        return moduleID;
    }

    public void setModuleID(String moduleID) {
        this.moduleID = moduleID;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public Map<String, String> getStudentsAttended() {
        return studentsAttended;
    }

    public void setStudentsAttended(Map<String, String> studentsAttended) {
        this.studentsAttended = studentsAttended;
    }
}
