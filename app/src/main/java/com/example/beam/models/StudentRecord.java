package com.example.beam.models;

import java.util.Map;

public class StudentRecord {
    private String studentID;
    private String moduleID;
    private Map<String, String> attendance;

    public StudentRecord() {}

    public StudentRecord(String studentID, String moduleID, Map<String, String> attendance) {
        this.studentID = studentID;
        this.moduleID = moduleID;
        this.attendance = attendance;
    }
}
