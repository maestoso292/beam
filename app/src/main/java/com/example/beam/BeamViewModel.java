package com.example.beam;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.beam.models.BeamUser;
import com.example.beam.models.Lecturer;
import com.example.beam.models.Record;
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
import java.util.TimeZone;

public class BeamViewModel extends ViewModel {
    private final static String LOG_TAG = "BeamViewModel";

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private MutableLiveData<BeamUser> userDetails;
    private MutableLiveData<Map<String, String>> userModules;
    private MutableLiveData<TimeTable> userWeeklyTimetable;

    private MutableLiveData<List<Session>> userModuleSessions;

    private MutableLiveData<List<? extends Record>> userRecord;

    public BeamViewModel() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void initialLoad() {
        loadUser();

        userModules = new MutableLiveData<>();
        userWeeklyTimetable = new MutableLiveData<>();
        userModuleSessions = new MutableLiveData<>();
        userRecord = new MutableLiveData<>();

        loadUserModules();
        loadUserWeeklyTimetable();

        String role = userDetails.getValue().getRole();
        if (role.equals("Student")) {
            loadStudentRecord();
        }
        else if (role.equals("Lecturer")) {
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

        List<String> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));

        int calendarOffset = 0;
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.TUESDAY:
                calendarOffset = 1;
                break;
            case Calendar.WEDNESDAY:
                calendarOffset = 2;
                break;
            case Calendar.THURSDAY:
                calendarOffset = 3;
                break;
            case Calendar.FRIDAY:
                calendarOffset = 4;
                break;
            case Calendar.SATURDAY:
                calendarOffset = 5;
                break;
            case Calendar.SUNDAY:
                calendarOffset = 6;
                break;
        }
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - calendarOffset);


        for (int i = 0; i < 7; i++) {
            dates.add(String.format("%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH)));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        Log.d(LOG_TAG, dates.toString());

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
                    if (timeTable.getWeeklyTimetable().size() == 7) {
                        userWeeklyTimetable.setValue(timeTable);
                    }
                    Log.d(LOG_TAG, "User Daily Timetable " + date + ": " + timeTable.getDailyTimetable(date));
                    Log.d(LOG_TAG, "Timetable Size: " + timeTable.getWeeklyTimetable().size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(LOG_TAG, "Error loading timetable: " + error);
                }
            });
        }
    }

    public LiveData<List<? extends Record>> getUserRecord() {
        if (userRecord == null) {
            userRecord = new MutableLiveData<>();

            String role = userDetails.getValue().getRole();
            if (role.equals("Student")) {
                loadStudentRecord();
            }
            else if (role.equals("Lecturer")) {
                loadLecturerRecord();
            }
        }
        return userRecord;
    }

    private void loadStudentRecord() {
        final List<StudentModuleRecord> tempList = new ArrayList<>();
        userRecord.setValue(tempList);
        for (final String moduleCode : userDetails.getValue().getModules().values()) {
            mDatabase.child("student_record").child(currentUser.getUid()).child(moduleCode).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GenericTypeIndicator<Map<String, Boolean>> t = new GenericTypeIndicator<Map<String, Boolean>>() {};
                    StudentModuleRecord record = new StudentModuleRecord(moduleCode, snapshot.getValue(t));
                    tempList.add(record);
                    userRecord.setValue(tempList);
                    Log.d(LOG_TAG, record.toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(LOG_TAG, "Error Loading Student Record: " + error);
                }
            });
        }
    }

    private void loadLecturerRecord() {
        // TODO Implement to fetch data from /record/
    }

    public MutableLiveData<List<Session>> getUserModuleSessions(String moduleCode) {
        try {
            final ArrayList<Session> list = new ArrayList<>();
            mDatabase.child("modules_session").child(moduleCode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GenericTypeIndicator<Map<String, Session>> t = new GenericTypeIndicator<Map<String, Session>>() {};
                    Map<String, Session> temp = snapshot.getValue(t);
                    for (Session session : temp.values()) {
                        list.add(session);
                        userModuleSessions.setValue(list);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        catch (NullPointerException exception) {
            Log.d(LOG_TAG, exception.toString());
        }
        return userModuleSessions;
    }
}
