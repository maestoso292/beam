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
}
