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

    @Override
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

    @Override
    public int getNumTotal() {
        try {
            return attendance.size();
        }
        catch (NullPointerException e) {
            return 0;
        }
    }

    @Override
    public int getNumAttended() {
        int num = 0;
        try {
            for (Boolean bool : attendance.values()) {
                num += bool ? 1 : 0;
            }
        }
        catch (NullPointerException e) {
            num = 0;
        }
        return num;
    }

    @Override
    public int getPercentageAttended() {
        if (getNumTotal() <= 0) {
            return -1;
        }
        else {
            return (int) Math.round(getNumAttended() / ((double) getNumTotal()) * 100);
        }
    }

    @Override
    public String getPercentageString() {
        int percentage = getPercentageAttended();
        if (percentage < 0) {
            return "N/A";
        }
        else {
            return "" + percentage + "%";
        }
    }
}
