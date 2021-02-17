package com.example.beam.models;

import java.util.Map;

public class Plan {
    private String name;
    private Map<String, String> modules;

    public Plan() {}

    public Plan(String name, Map<String, String> modules) {
        this.name = name;
        this.modules = modules;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getModules() {
        return modules;
    }

    public void setModules(Map<String, String> modules) {
        this.modules = modules;
    }
}
