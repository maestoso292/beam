package com.example.beam.models;

import java.util.Map;

public abstract class BeamUser {
    private String email;
    private String firstName;
    private String lastName;
    private String Role;
    private Map<String, String> modules;

    public BeamUser() {}

    public BeamUser(String email, String firstName, String lastName, String role, Map<String, String> modules) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.Role = role;
        this.modules = modules;
    }

    @Override
    public String toString() {
        return "BeamUser{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role='" + Role + '\'' +
                ", modules=" + modules +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        this.Role = role;
    }

    public Map<String, String> getModules() {
        return modules;
    }

    public void setModules(Map<String, String> modules) {
        this.modules = modules;
    }
}
