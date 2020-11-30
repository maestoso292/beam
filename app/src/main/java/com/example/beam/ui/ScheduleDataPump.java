package com.example.beam.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ScheduleDataPump {
    // TODO This class is meant for populating the list in schedule. Fetch from database and populate
    public static class TestModule {
        public String name;
        public String time;

        TestModule(String name, String time) {
            this.name = name;
            this.time = time;
        }
    }

    public static final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    public static LinkedHashMap<String, List<TestModule>> getData() {
        LinkedHashMap<String, List<TestModule>> expandableListData = new LinkedHashMap<>(5);
        for (String day : days) {
            int size = ThreadLocalRandom.current().nextInt(0, 5);
            ArrayList<TestModule> sessions = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                sessions.add(new TestModule("Sample Module Name", "10:00 - 12:00"));
            }
            expandableListData.put(day, sessions);
        }
        return expandableListData;
    }
}
