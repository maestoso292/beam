package com.example.beam.models;

import java.util.Map;

public class Student extends BeamUser{
    private String programme;

    public Student() {
        super();
    }

    public Student(String email, String firstName, String lastName, String programme, String role, Map<String, String> modules) {
        super(email, firstName, lastName, role, modules);
        this.programme = programme;
    }

    @Override
    public String toString() {
        return "Student{" +
                "programme='" + programme + '\'' +
                ", email='" + getEmail() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", role='" + getRole() + '\'' +
                ", modules=" + getModules() +
                '}';
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }
}
