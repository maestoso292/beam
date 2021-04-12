package com.example.beam.models;

public class Session implements Comparable<Session> {
    private String module_id;
    private String session_id;
    private String sessionType;
    private String time_begin;
    private String time_end;
    private String status;

    public Session() {}

    public Session(String module_id, String session_id, String sessionType, String time_begin, String time_end, String status) {
        this.module_id = module_id;
        this.session_id = session_id;
        this.sessionType = sessionType;
        this.time_begin = time_begin;
        this.time_end = time_end;
        this.status = status;
    }

    public Session(String module_id, String sessionType, String time_begin, String time_end, String status) {
        this.module_id = module_id;
        this.sessionType = sessionType;
        this.time_begin = time_begin;
        this.time_end = time_end;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Session{" +
                "moduleCode='" + module_id + '\'' +
                ", sessionType='" + sessionType + '\'' +
                ", timeBegin='" + time_begin + '\'' +
                ", timeEnd='" + time_end + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public int compareTo(Session session) {
        return this.time_begin.compareTo(session.time_begin);
    }

    public String getModule_id() {
        return module_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public String getTime_begin() {
        return time_begin;
    }

    public void setTime_begin(String time_begin) {
        this.time_begin = time_begin;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
