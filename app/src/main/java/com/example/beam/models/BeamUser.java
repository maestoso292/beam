package com.example.beam.models;

import java.util.Map;

public abstract class BeamUser {
    public String email;
    public String firstName;
    public String lastName;
    public String role;
    public Map<String, String> modules;

    public BeamUser() {

    }

    public BeamUser(String email, String firstName, String lastName, String role, Map<String, String> modules) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.modules = modules;
    }

    @Override
    public String toString() {
        return "BeamUser{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role='" + role + '\'' +
                ", modules=" + modules +
                '}';
    }
}
