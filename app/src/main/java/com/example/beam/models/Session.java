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
}
