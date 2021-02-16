package com.example.beam.models;

import java.util.Map;

public class Student extends BeamUser{
    public String programme;

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
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role='" + role + '\'' +
                ", modules=" + modules +
                '}';
    }
}
