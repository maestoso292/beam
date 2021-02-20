package com.example.beam;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.beam.models.BeamUser;
import com.example.beam.models.Lecturer;
import com.example.beam.models.LecturerModuleRecord;
import com.example.beam.models.Session;
import com.example.beam.models.Student;
import com.example.beam.models.StudentModuleRecord;
import com.example.beam.models.TimeTable;
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
    private MutableLiveData<TimeTable> userWeeklyTimetable;

    private MutableLiveData<List<StudentModuleRecord>> studentRecord;
    private MutableLiveData<List<LecturerModuleRecord>> lecturerRecord;

    public BeamViewModel() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void initialLoad() {
        loadUser();

        userModules = new MutableLiveData<>();
        userWeeklyTimetable = new MutableLiveData<>();

        loadUserModules();
        loadUserWeeklyTimetable();

        String role = userDetails.getValue().getRole();
        if (role.equals("Student")) {
            studentRecord = new MutableLiveData<>();
            loadStudentRecord();
        }
        else if (role.equals("Lecturer")) {
            lecturerRecord = new MutableLiveData<>();
            loadLecturerRecord();
        }
    }

    public void loadUser() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
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
                Log.d(LOG_TAG, "User Details: " + userDetails.getValue());
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
        List<String> moduleCodes = new ArrayList<>(userDetails.getValue().getModules().values());
        Collections.sort(moduleCodes);
        userModules.setValue(new LinkedHashMap<String, String>());
        final Map<String, String> tempModules = new LinkedHashMap<>();
        for (final String moduleCode : moduleCodes) {
            mDatabase.child("modules").child(moduleCode).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    tempModules.put(moduleCode, snapshot.getValue(String.class));
                    userModules.setValue(tempModules);
                    Log.d(LOG_TAG, "User Modules: " + userModules.getValue().values());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(LOG_TAG, "Error Loading User Modules: " + error);
                }
            });
        }
    }

    public LiveData<TimeTable> getUserWeeklyTimetable() {
        if (userWeeklyTimetable == null) {
            userWeeklyTimetable = new MutableLiveData<>();
            loadUserWeeklyTimetable();
        }
        return userWeeklyTimetable;
    }

    private void loadUserWeeklyTimetable() {
        final TimeTable timeTable = new TimeTable(new HashMap<String, List<Session>>());
        userWeeklyTimetable.setValue(timeTable);

        List<String> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        for (int i = 0; i < 7; i++) {
            dates.add(String.format("%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH)+i));
        }

        for (final String date : dates) {
            mDatabase.child("timetable").child(date).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GenericTypeIndicator<Map<String, Map<String, Session>>> t = new GenericTypeIndicator<Map<String, Map<String, Session>>>() {};
                    // Module, SessionID, Session
                    Map<String, Map<String, Session>> moduleSessionsMap = snapshot.getValue(t);
                    List<Session> sessions = new ArrayList<>();
                    for (Map<String, Session> map : moduleSessionsMap.values()) {
                        sessions.addAll(map.values());
                    }
                    Collections.sort(sessions);
                    timeTable.putDailyTimetable(date, sessions);
                    userWeeklyTimetable.setValue(timeTable);
                    Log.d(LOG_TAG, "User Daily Timetable " + date + ": " + userWeeklyTimetable.getValue().getDailyTimetable(date));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(LOG_TAG, "Error loading timetable: " + error);
                }
            });
        }
    }

    public LiveData<List<StudentModuleRecord>> getStudentRecord() {
        if (studentRecord == null) {
            studentRecord = new MutableLiveData<>();
            loadStudentRecord();
        }
        return studentRecord;
    }

    private void loadStudentRecord() {
        final List<StudentModuleRecord> tempList = new ArrayList<>();
        studentRecord.setValue(tempList);
        for (final String moduleCode : userDetails.getValue().getModules().values()) {
            mDatabase.child("student_record").child(currentUser.getUid()).child(moduleCode).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GenericTypeIndicator<Map<String, Boolean>> t = new GenericTypeIndicator<Map<String, Boolean>>() {};
                    StudentModuleRecord record = new StudentModuleRecord(moduleCode, snapshot.getValue(t));
                    tempList.add(record);
                    studentRecord.setValue(tempList);
                    Log.d(LOG_TAG, record.toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(LOG_TAG, "Error Loading Student Record: " + error);
                }
            });
        }
    }

    public LiveData<List<LecturerModuleRecord>> getLecturerRecord() {
        if (studentRecord == null) {
            studentRecord = new MutableLiveData<>();
            loadStudentRecord();
        }
        return lecturerRecord;
    }

    private void loadLecturerRecord() {
    }
}
