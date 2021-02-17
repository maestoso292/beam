package com.example.beam.models;

public class Session implements Comparable<Session> {
    private String moduleID;
    private String sessionID;
    private String sessionType;
    private String timeBegin;
    private String timeEnd;
    private String status;

    public Session() {}

    public Session(String moduleID, String sessionID, String sessionType, String timeBegin, String timeEnd, String status) {
        this.moduleID = moduleID;
        this.sessionID = sessionID;
        this.sessionType = sessionType;
        this.timeBegin = timeBegin;
        this.timeEnd = timeEnd;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Session{" +
                "moduleCode='" + moduleCode + '\'' +
                ", sessionType='" + sessionType + '\'' +
                ", timeBegin='" + timeBegin + '\'' +
                ", timeEnd='" + timeEnd + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public int compareTo(Session session) {
        return this.timeBegin.compareTo(session.timeBegin);
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

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public String getTimeBegin() {
        return timeBegin;
    }

    public void setTimeBegin(String timeBegin) {
        this.timeBegin = timeBegin;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
