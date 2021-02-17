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

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getModuleID() {
        return moduleID;
    }

    public void setModuleID(String moduleID) {
        this.moduleID = moduleID;
    }

    public Map<String, String> getAttendance() {
        return attendance;
    }

    public void setAttendance(Map<String, String> attendance) {
        this.attendance = attendance;
    }
}
