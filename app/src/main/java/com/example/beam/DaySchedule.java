package com.example.beam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DaySchedule {
    public List<Session> daySessions;

    public DaySchedule() {
        daySessions = new ArrayList<>();
    }

    public void addModuleSessions(String moduleCode, List<Session> sessions) {
        for (Session session : sessions) {
            session.setModuleCode(moduleCode);
            daySessions.add(session);
        }
        Collections.sort(daySessions);
    }

    public int getNumSessions() {
        return  daySessions.size();
    }

    @Override
    public String toString() {
        return "DaySchedule{" +
                "daySessions=" + daySessions +
                '}';
    }
}
