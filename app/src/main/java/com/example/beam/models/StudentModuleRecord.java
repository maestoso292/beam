package com.example.beam.models;

import java.util.Map;

public class StudentModuleRecord implements Comparable<StudentModuleRecord>, Record{
    private String moduleID;
    private Map<String, Boolean> attendance;

    public StudentModuleRecord() {}

    public StudentModuleRecord(String moduleID, Map<String, Boolean> attendance) {
        this.moduleID = moduleID;
        this.attendance = attendance;
    }

    @Override
    public String toString() {
        return "StudentModuleRecord{" +
                "moduleID='" + moduleID + '\'' +
                ", attendance=" + attendance +
                '}';
    }

    public String getModuleID() {
        return moduleID;
    }

    public void setModuleID(String moduleID) {
        this.moduleID = moduleID;
    }

    public Map<String, Boolean> getAttendance() {
        return attendance;
    }

    public void setAttendance(Map<String, Boolean> attendance) {
        this.attendance = attendance;
    }

    @Override
    public int compareTo(StudentModuleRecord studentModuleRecord) {
        return moduleID.compareTo(studentModuleRecord.getModuleID());
    }
}
