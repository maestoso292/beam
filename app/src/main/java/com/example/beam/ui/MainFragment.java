package com.example.beam.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.beam.BeamViewModel;
import com.example.beam.R;
import com.example.beam.models.BeamUser;
import com.example.beam.models.Session;
import com.example.beam.models.TimeTable;
import com.example.beam.services.BeamBroadcastReceiver;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MainFragment extends Fragment {
    private ViewPager2 pager;
    private TabLayout tabLayout;
    private MainFragmentAdapter adapter;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private NavController navController;
    private BeamViewModel beamViewModel;

    private BeamUser userDetails;
    private Map<String, String> userModules;

    private AlarmManager alarmManager;

    private boolean firstLoad;
    private static int temp=0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstLoad = false;
        navController = NavHostFragment.findNavController(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        beamViewModel = new ViewModelProvider(getActivity()).get(BeamViewModel.class);

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    navController.navigate(R.id.splashFragment);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        ActionMenuItemView settingsButton = view.findViewById(R.id.toolbar_settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MainFragment.this).navigate(R.id.settings_dest);
            }
        });

        pager = view.findViewById(R.id.main_pager);
        tabLayout = view.findViewById(R.id.tab_layout);
        adapter = new MainFragmentAdapter(this);

        pager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

            }
        }).attach();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!firstLoad) {
            firstLoad = true;
            navController.navigate(R.id.splashFragment);
        }

        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            navController.navigate(R.id.splashFragment);


            /*
            // Manual timetable insertion
            Calendar calendar;
            // 12 weeks of class
            for (int i = 0; i < 12; i++) {
                calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
                calendar.add(Calendar.WEEK_OF_YEAR, i);
                generateTimetable(calendar);
            }
             */
        }
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        final String date = String.format(Locale.ENGLISH, "%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
        final String currentTime = String.format("%02d%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        beamViewModel.getUserWeeklyTimetable().observe(getViewLifecycleOwner(), new Observer<TimeTable>() {
            @Override
            public void onChanged(TimeTable timeTable) {
                if (timeTable.getWeeklyTimetable().size() != 7) {
                    return;
                }

                try {
                    List<Session> sessions = timeTable.getDailyTimetable(date);
                    for (Session session : sessions) {
                        /*
                        if (currentTime.compareTo(session.getTimeBegin()) > 0) {
                            continue;
                        }

                        long sessionBeginMillisecond = getMillisecondForSessionTime(session.getTimeBegin());
                        long sessionEndMillisecond = getMillisecondForSessionTime(session.getTimeEnd());

                        Map<String, String> extras = new HashMap<>();
                        extras.put("token", session.getSessionID());

                        PendingIntent startCentralPIntent = getPIntentForServiceBroadcast(BeamBroadcastReceiver.CENTRAL_SERVICE, BeamBroadcastReceiver.START_SERVICE, extras, 0);
                        PendingIntent stopCentralPIntent = getPIntentForServiceBroadcast(BeamBroadcastReceiver.CENTRAL_SERVICE, BeamBroadcastReceiver.STOP_SERVICE, null, 0);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, sessionBeginMillisecond, startCentralPIntent);
                            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, sessionEndMillisecond, stopCentralPIntent);
                        }
                        else {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, sessionBeginMillisecond, startCentralPIntent);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, sessionEndMillisecond, stopCentralPIntent);
                        }
                         */
                    }
                }
                catch (NullPointerException e) {

                }
            }
        });
    }

    /**
     * Obtain the corresponding millisecond value of the sessionTime argument
     * @param sessionTime String representing time, format: HHMM
     * @return Corresponding millisecond
     */
    private long getMillisecondForSessionTime(String sessionTime) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));

        int sessionBeginHour = Integer.parseInt(sessionTime.substring(0, 2),10);
        int sessionBeginMinute = Integer.parseInt(sessionTime.substring(2),10);

        calendar.set(Calendar.HOUR_OF_DAY, sessionBeginHour);
        calendar.set(Calendar.MINUTE, sessionBeginMinute);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();
    }

    /**
     * Obtain a PendingIntent that starts a Beam background service. Only for Beam background services.
     * @param service Type of service to start
     * @param command Whether to start or stop the service
     * @param extras String extras to put in the PendingIntent
     * @param flags Flags for PendingIntent.getBroadcast()
     * @return PendingIntent to start a Beam background service
     */
    private PendingIntent getPIntentForServiceBroadcast(int service, int command, @Nullable Map<String, String> extras, int flags) {
        Intent intent = new Intent(getContext(), BeamBroadcastReceiver.class);
        intent.putExtra("service", service);
        intent.putExtra("command", command);
        if (extras != null) {
            for (Map.Entry<String, String> entry : extras.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        }
        return PendingIntent.getBroadcast(getContext(), service + command, intent, flags);
    }

    public void addToDatabase(String date, Map<String, List<Session>> map) {
        for (Map.Entry<String, List<Session>> entry : map.entrySet()) {

            for (Session session : entry.getValue()) {
                DatabaseReference ref = mDatabase.child("timetable").child(date).child(entry.getKey()).push();
                session.setSessionID(ref.getKey());
                ref.setValue(session);
            }
        }
    }

    public void generateTimetable(Calendar calendar) {
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        List<Session> sessions;
        Map<String, List<Session>> map;
        String date;
        map = new HashMap<>();
        sessions = new ArrayList<>();
        // MONDAY
        map = new HashMap<>();

        sessions = new ArrayList<>();
        sessions.add(new Session("COMP1000", "Computing", "0900", "1100", "Unavailable"));
        sessions.add(new Session("COMP1000", "Tutorial", "1600", "1800", "Unavailable"));
        map.put("COMP1000", sessions);

        sessions = new ArrayList<>();
        sessions.add(new Session("COMP1002", "Computing", "1100", "1300", "Unavailable"));
        sessions.add(new Session("COMP1002", "Computing", "1400", "1600", "Unavailable"));
        map.put("COMP1002", sessions);

        date = String.format("%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
        addToDatabase(date, map);

        // TUESDAY
        calendar.add(Calendar.DATE, 1);
        map = new HashMap<>();

        sessions = new ArrayList<>();
        sessions.add(new Session("COMP1000", "Computing", "1100", "1300", "Unavailable"));
        map.put("COMP1000", sessions);

        sessions = new ArrayList<>();
        sessions.add(new Session("COMP1002", "Lecture", "1600", "1700", "Unavailable"));
        map.put("COMP1002", sessions);

        sessions = new ArrayList<>();
        sessions.add(new Session("COMP1003", "Lecture", "1400", "1600", "Unavailable"));
        map.put("COMP1003", sessions);

        date = String.format("%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
        addToDatabase(date, map);

        // WEDNESDAY
        calendar.add(Calendar.DATE, 1);
        map = new HashMap<>();

        sessions = new ArrayList<>();
        sessions.add(new Session("COMP1001", "Lecture", "1400", "1600", "Unavailable"));
        sessions.add(new Session("COMP1001", "Tutorial", "1600", "1700", "Unavailable"));
        map.put("COMP1001", sessions);

        sessions = new ArrayList<>();
        sessions.add(new Session("COMP1002", "Lecture", "1100", "1300", "Unavailable"));
        map.put("COMP1002", sessions);

        date = String.format("%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
        addToDatabase(date, map);

        // THURSDAY
        calendar.add(Calendar.DATE, 1);
        map = new HashMap<>();

        sessions = new ArrayList<>();
        sessions.add(new Session("COMP1003", "Computing", "0900", "1100", "Unavailable"));
        sessions.add(new Session("COMP1003", "Computing", "1100", "1300", "Unavailable"));
        map.put("COMP1003", sessions);

        date = String.format("%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
        addToDatabase(date, map);

        // FRIDAY
        calendar.add(Calendar.DATE, 1);
        map = new HashMap<>();

        sessions = new ArrayList<>();
        sessions.add(new Session("COMP1000", "Lecture", "1000", "1200", "Unavailable"));
        map.put("COMP1000", sessions);

        date = String.format("%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
        addToDatabase(date, map);

        // SATURDAY
        calendar.add(Calendar.DATE, 1);
        map = new HashMap<>();

        sessions = new ArrayList<>();
        sessions.add(new Session("MLAC1007", "Lecture", "1100", "1300", "Unavailable"));
        map.put("MLAC1007", sessions);

        date = String.format("%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
        addToDatabase(date, map);

        // SUNDAY
        calendar.add(Calendar.DATE, 1);
        map = new HashMap<>();

        sessions = new ArrayList<>();
        sessions.add(new Session("MLAC1048", "Seminar", "0900", "1100", "Unavailable"));
        map.put("MLAC1048", sessions);

        date = String.format("%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
        addToDatabase(date, map);
    }
}
