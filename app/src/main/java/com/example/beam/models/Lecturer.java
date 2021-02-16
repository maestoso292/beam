package com.example.beam.models;

import java.util.Map;

public class Lecturer extends BeamUser{
    private String faculty;
    private String position;

    public Lecturer() {
        super();
    }

    public Lecturer(String email, String faculty, String firstName, String lastName, String position, String role, Map<String, String> modules) {
        super(email, firstName, lastName, role, modules);
        this.faculty = faculty;
        this.position = position;
    }

    @Override
    public String toString() {
        return "Lecturer{" +
                "faculty='" + faculty + '\'' +
                ", position='" + position + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role='" + role + '\'' +
                ", modules=" + modules +
                '}';
    }
}
