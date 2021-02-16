package com.example.beam.models;

import java.util.Map;

public class Module {
    private String name;
    private Map<String, String> students;

    public Module() {}

    public Module(String name, Map<String, String> students) {
        this.name = name;
        this.students = students;
    }
}
