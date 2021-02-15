package com.example.beam;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BeamViewModel extends ViewModel {
    private final static String LOG_TAG = "BeamViewModel";
    private static final String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private MutableLiveData<List<String>> userModuleCodes;
    private MutableLiveData<Map<String, String>> userModules;
    private MutableLiveData<Map<String, DaySchedule>> userSchedule;

    public BeamViewModel() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public LiveData<List<String>> getUserModuleCodes() {
        if (userModuleCodes == null) {
            userModuleCodes = new MutableLiveData<>();
            loadUserModuleCodes();
        }
        return userModuleCodes;
    }

    public LiveData<Map<String, String>> getUserModules() {
        if (userModules == null) {
            userModules = new MutableLiveData<>();
            loadUserModuleCodes();
        }
        return userModules;
    }

    public LiveData<Map<String, DaySchedule>> getUserSchedule() {
        if (userSchedule == null) {
            userSchedule = new MutableLiveData<>();
            loadUserSchedule();
        }
        return userSchedule;
    }

    public void initialLoad() {
        if (currentUser != null) {
            loadUserModuleCodes();
        }
    }

    private void loadUserModuleCodes() {
        if (userModuleCodes == null) {
            userModuleCodes = new MutableLiveData<>();
        }
        mDatabase.child("student").child(currentUser.getUid()).child("modules").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<Map<String, String>> t = new GenericTypeIndicator<Map<String, String>>() {};
                userModuleCodes.setValue(new ArrayList<>(snapshot.getValue(t).values()));
                Log.d(LOG_TAG, "Module Codes: " + userModuleCodes.getValue());
                loadUserModules();
                loadUserSchedule();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(LOG_TAG, "Error: " + error);
            }
        });
    }

    private void loadUserModules() {
        if (userModules == null) {
            userModules = new MutableLiveData<>();
        }
        final Map<String, String> temp = new HashMap<>();
        for (final String moduleCode : userModuleCodes.getValue()) {
            mDatabase.child("modules").child(moduleCode).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    temp.put(moduleCode, snapshot.getValue(String.class));
                    userModules.setValue(temp);
                    Log.d(LOG_TAG,"Module List: " + temp.toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(LOG_TAG, "Error: " + error);
                }
            });
        }
    }

    private void loadUserSchedule() {
        if (userSchedule == null) {
            userSchedule = new MutableLiveData<>();
        }
        final Map<String, DaySchedule> dayScheduleTemp = new LinkedHashMap<>();
        userSchedule.setValue(dayScheduleTemp);
        for (final String day : days) {
            userSchedule.getValue().put(day, new DaySchedule());
            /*
            mDatabase.child("timetable").child(day).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GenericTypeIndicator<Map<String, Map<String, Session>>> t = new GenericTypeIndicator<Map<String, Map<String, Session>>>() {};
                    if (snapshot.getValue(t) != null) {
                        Log.d(LOG_TAG, snapshot.getValue().toString());

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

             */

            final Map<String, List<Session>> moduleSessionsTemp = new HashMap<>();
            for (final String moduleCode : userModuleCodes.getValue()) {
                mDatabase.child("timetable").child(day).child(moduleCode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GenericTypeIndicator<Map<String, Session>> t = new GenericTypeIndicator<Map<String, Session>>() {};
                        if (snapshot.getValue(t) != null) {
                            List<Session> sessionsTemp = new ArrayList<>(snapshot.getValue(t).values());
                            moduleSessionsTemp.put(moduleCode, sessionsTemp);
                            userSchedule.getValue().get(day).addModuleSessions(moduleCode, sessionsTemp);
                            Log.d(LOG_TAG + "Session", userSchedule.getValue().toString());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

}
