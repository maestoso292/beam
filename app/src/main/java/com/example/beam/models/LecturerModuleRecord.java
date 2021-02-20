package com.example.beam.models;

import java.util.List;

public class LecturerModuleRecord implements Record {
    String moduleID;
    List<LecturerSessionRecord> moduleAttendance;

    public LecturerModuleRecord() {
    }

    public LecturerModuleRecord(String moduleID, List<LecturerSessionRecord> moduleAttendance) {
        this.moduleID = moduleID;
        this.moduleAttendance = moduleAttendance;
    }

    public String getModuleID() {
        return moduleID;
    }

    public void setModuleID(String moduleID) {
        this.moduleID = moduleID;
    }

    public List<LecturerSessionRecord> getModuleAttendance() {
        return moduleAttendance;
    }

    public void setModuleAttendance(List<LecturerSessionRecord> moduleAttendance) {
        this.moduleAttendance = moduleAttendance;
    }
}
