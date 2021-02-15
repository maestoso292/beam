package com.example.beam;

public class Session implements Comparable<Session> {
    public String moduleCode;
    public String sessionType;
    public String timeBegin;
    public String timeEnd;

    public Session(String sessionType, String timeBegin, String timeEnd) {
        this.sessionType = sessionType;
        this.timeBegin = timeBegin;
        this.timeEnd = timeEnd;
    }

    public Session() {

    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    @Override
    public String toString() {
        return "Session{" +
                "sessionType='" + sessionType + '\'' +
                ", timeBegin='" + timeBegin + '\'' +
                ", timeEnd='" + timeEnd + '\'' +
                '}';
    }

    @Override
    public int compareTo(Session session) {
        return this.timeBegin.compareTo(session.timeBegin);
    }
}
