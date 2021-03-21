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
                ", email='" + getEmail() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", role='" + getRole() + '\'' +
                ", modules=" + getModules() +
                '}';
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
