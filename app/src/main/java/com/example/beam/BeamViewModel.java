package com.example.beam;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.beam.models.BeamUser;
import com.example.beam.models.Lecturer;
import com.example.beam.models.Session;
import com.example.beam.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BeamViewModel extends ViewModel {
    private final static String LOG_TAG = "BeamViewModel";

    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private MutableLiveData<BeamUser> userDetails;
    private MutableLiveData<Map<String, String>> userModules;
    private MutableLiveData<Map<String, Map<String, Map<String, Session>>>> userWeeklyTimetable;

    public BeamViewModel() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public LiveData<Map<String, Map<String, Map<String, Session>>>> getUserWeeklyTimetable() {
        if (userWeeklyTimetable == null) {
            userWeeklyTimetable = new MutableLiveData<>();
            loadUserWeeklyTimetable();
        }
        return userWeeklyTimetable;
    }

    private void loadUserWeeklyTimetable() {
        final Map<String, Map<String, Map<String, Session>>> tempMap = new HashMap<>();
        userWeeklyTimetable.setValue(tempMap);
        List<String> dates = new ArrayList<>();
        List<String> modules = new ArrayList<>(userDetails.getValue().modules.values());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        for (int i = 0; i < 5; i++) {
            dates.add(String.format("%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)+i));
        }
        for (final String date : dates) {
            tempMap.put(date, new HashMap<String, Map<String, Session>>());
            for(final String module : modules) {
                mDatabase.child("timetableTest").child(date).child(module).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GenericTypeIndicator<Map<String, Session>> t = new GenericTypeIndicator<Map<String, Session>>() {};
                        Map<String, Session> sessionMap = snapshot.getValue(t);
                        tempMap.get(date).put(module, sessionMap);
                        userWeeklyTimetable.setValue(tempMap);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    public LiveData<BeamUser> getUserDetails() {
        if (userDetails == null) {
            userDetails = new MutableLiveData<>();
            loadUserDetails();
        }
        return userDetails;
    }

    private void loadUserDetails() {
        mDatabase.child("users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("role").getValue(String.class).equals("Student")) {
                    userDetails.setValue(snapshot.getValue(Student.class));
                }
                else {
                    userDetails.setValue(snapshot.getValue(Lecturer.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(LOG_TAG, "Error Loading User Details: " + error);
            }
        });
    }

    public LiveData<Map<String, String>> getUserModules() {
        if (userModules == null) {
            userModules = new MutableLiveData<>();
            loadUserModules();
        }
        return userModules;
    }

    private void loadUserModules() {
        List<String> moduleCodes = new ArrayList<>(userDetails.getValue().modules.values());
        Collections.sort(moduleCodes);
        userModules.setValue(new LinkedHashMap<String, String>());
        final Map<String, String> tempModules = new LinkedHashMap<>();
        for (final String moduleCode : moduleCodes) {
            mDatabase.child("modules").child(moduleCode).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    tempModules.put(moduleCode, snapshot.getValue(String.class));
                    userModules.setValue(tempModules);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(LOG_TAG, "Error Loading User Modules: " + error);
                }
            });
        }
    }
}