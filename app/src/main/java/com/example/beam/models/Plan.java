package com.example.beam.models;

import java.util.List;

public class Plan {
    private String name;
    private List<String> modules;

    public Plan() {}

    public Plan(String name, List<String> modules) {
        this.name = name;
        this.modules = modules;
    }

    @Override
    public String toString() {
        return "Plan{" +
                "name='" + name + '\'' +
                ", modules='" + modules +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getModules() {
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }
}
