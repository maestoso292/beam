package com.example.beam.models;

public class Session implements Comparable<Session> {
    public String moduleCode;
    public String sessionType;
    public String timeBegin;
    public String timeEnd;



    public Session() {

    }

    public Session(String moduleCode, String sessionType, String timeBegin, String timeEnd) {
        this.moduleCode = moduleCode;
        this.sessionType = sessionType;
        this.timeBegin = timeBegin;
        this.timeEnd = timeEnd;
    }


    @Override
    public String toString() {
        return "Session{" +
                "moduleCode='" + moduleCode + '\'' +
                ", sessionType='" + sessionType + '\'' +
                ", timeBegin='" + timeBegin + '\'' +
                ", timeEnd='" + timeEnd + '\'' +
                '}';
    }

    @Override
    public int compareTo(Session session) {
        return this.timeBegin.compareTo(session.timeBegin);
    }
}
