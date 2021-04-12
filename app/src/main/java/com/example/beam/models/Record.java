package com.example.beam.models;

public interface Record{
    String getModuleID();

    int getNumTotal();

    int getNumAttended();

    int getPercentageAttended();

    String getPercentageString();
}
