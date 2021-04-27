package com.example.beam.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.example.beam.models.Session;
import com.example.beam.models.TimeTable;
import com.example.beam.services.BeamBroadcastReceiver;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Fragment subclass for implementation of MainScreen. Contains ViewPager2 consisting of 4 Fragments.
 * Responsible for scheduling broadcasts to start/stop services at session time.
 */
public class MainFragment extends Fragment {
    /** Instance of ViewPager2 that should contain 4 Fragments that can be swiped between */
    private ViewPager2 pager;
    // TODO Tab headings may be unnecessary.
    /** Tab headings of each Fragment in ViewPager2 */
    private TabLayout tabLayout;
    /** Adapter for ViewPager2. */
    private MainFragmentAdapter adapter;
    /** Firebase Authentication instance for getting authentication state */
    private FirebaseAuth mAuth;
    /** Current authentication state */
    private FirebaseUser currentUser;
    /** Navigation controller to navigate to different screens */
    private NavController navController;
    /** BeamViewModel instance for obtaining relevant data from database*/
    private BeamViewModel beamViewModel;
    /** AlarmManager instance for scheduling broadcasts */
    private AlarmManager alarmManager;
    /** Boolean of whether this is first load of the app */
    private boolean firstLoad;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstLoad = true;
        navController = NavHostFragment.findNavController(this);
        mAuth = FirebaseAuth.getInstance();
        beamViewModel = new ViewModelProvider(getActivity()).get(BeamViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    /**
     * Add functionality to settings icon. Obtain ViewPager2 instance and sets new adapter. Attach
     * tab headings.
     * @param view XML view of Fragment
     * @param savedInstanceState Previous saved state of Fragment
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        // On press of settings icon, navigate to Settings Screen
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

        // Set adapter and tab headings for ViewPager2
        pager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

            }
        }).attach();
    }

    /**
     * On arrival at MainFragment, navigate to Splash Screen on first load/no authentication
     * or schedule broadcasts at sessions start/end times.
     */
    @Override
    public void onResume() {
        super.onResume();
        currentUser = mAuth.getCurrentUser();
        // On first load or with no authentication state, navigate to Splash Screen
        if (beamViewModel.isFirstLoad() || currentUser == null) {
            Log.d("MainFragment", "Redirecting to Splash");
            beamViewModel.setFirstLoad(false);
            navController.navigate(R.id.splashFragment);
        }
        else {
            // Set to display HomeFragment first
            pager.setCurrentItem(0);

            // Obtain current date and time based on Malaysian time
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
            final String date = String.format(Locale.ENGLISH, "%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
            final String currentTime = String.format(Locale.ENGLISH, "%02d%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

            Log.d("MainFragment", "Loading Timetable");
            // Load weekly timetable
            beamViewModel.getUserWeeklyTimetable().observe(getViewLifecycleOwner(), new Observer<TimeTable>() {
                @Override
                public void onChanged(TimeTable timeTable) {
                    Log.d("MainFragment", date + ": " + timeTable.getWeeklyTimetable().size());

                    if (timeTable.getWeeklyTimetable().size() != 7) {
                        Log.d("MainFragment", "timetable not large enough: " + timeTable.getWeeklyTimetable().size());
                        return;
                    }

                    try {
                        // Get daily timetable
                        List<Session> sessions = timeTable.getDailyTimetable(date);
                        Log.d("MainFragment", "Sessions Size: " + sessions.size());
                        // For each session, schedule corresponding broadcasts
                        for (Session session : sessions) {
                            if (currentTime.compareTo(session.getTime_begin()) > 0) {
                                Log.d("MainFragment", "Current time > session time");
                                continue;
                            }
                            // Get corresponding OS time for session start and end
                            long sessionBeginMillisecond = getMillisecondForSessionTime(session.getTime_begin());
                            long sessionEndMillisecond = getMillisecondForSessionTime(session.getTime_end());

                            Map<String, String> extras = new HashMap<>();
                            extras.put("moduleId", session.getModule_id());
                            extras.put("sessionId", session.getSession_id());

                            PendingIntent startPIntent;
                            PendingIntent stopPIntent;
                            String userRole = beamViewModel.getUserDetails().getValue().getRole();
                            // If student, schedule broadcasts for CentralService.
                            // Else if lecturer, schedule broadcasts for Open and Close AttendanceService
                            // Else, something has gone wrong.
                            if (userRole.equals("Student")) {
                                startPIntent = getPIntentForServiceBroadcast(session.getTime_begin(), BeamBroadcastReceiver.CENTRAL_SERVICE, BeamBroadcastReceiver.START_SERVICE, extras);
                                stopPIntent = getPIntentForServiceBroadcast(session.getTime_end(), BeamBroadcastReceiver.CENTRAL_SERVICE, BeamBroadcastReceiver.STOP_SERVICE, extras);
                            }
                            else if (userRole.equals("Lecturer")) {
                                startPIntent = getPIntentForServiceBroadcast(session.getTime_begin(), BeamBroadcastReceiver.OPEN_ATTENDANCE, BeamBroadcastReceiver.START_SERVICE, extras);
                                stopPIntent = getPIntentForServiceBroadcast(session.getTime_end(), BeamBroadcastReceiver.CLOSE_ATTENDANCE, BeamBroadcastReceiver.START_SERVICE, extras);
                            }
                            else {
                                Log.d("MainFragment", "No user role.");
                                Toast.makeText(getContext(), "No user role", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // If either intent is null, broadcast has already been scheduled for this session.
                            // Continue to broadcast for next session.
                            if (startPIntent==null) {
                                Log.d("MainFragment", "Start Intent Already Exists");
                                continue;
                            }
                            if (stopPIntent==null) {
                                Log.d("MainFragment", "Stop Intent Already Exists");
                                continue;
                            }

                            Log.d("MainFragment", "Service Alarm posted: " + sessionBeginMillisecond);
                            // Schedule broadcasts
                            // RTC_WAKEUP is to wake up the device on service start
                            // .setAndAllowWhileIdle is to allow broadcasts to be sent and handled while app is closed
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, sessionBeginMillisecond, startPIntent);
                                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, sessionEndMillisecond, stopPIntent);
                            }
                            else {
                                alarmManager.set(AlarmManager.RTC_WAKEUP, sessionBeginMillisecond, startPIntent);
                                alarmManager.set(AlarmManager.RTC_WAKEUP, sessionEndMillisecond, stopPIntent);
                            }
                        }
                        Toast.makeText(getContext(), "All session alarms set", Toast.LENGTH_SHORT).show();
                    }
                    catch (NullPointerException e) {
                        Log.d("MainFragment", "Error posting Alarm");
                    }
                }
            });
        }
    }

    /**
     * Obtain the corresponding millisecond value of the sessionTime argument
     * @param sessionTime String representing time, format: HHMM
     * @return Corresponding millisecond
     */
    private long getMillisecondForSessionTime(String sessionTime) {
        // Get current Malaysian time
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        // Convert 24 hour format to digit
        int sessionBeginHour = Integer.parseInt(sessionTime.substring(0, 2),10);
        int sessionBeginMinute = Integer.parseInt(sessionTime.substring(2),10);
        // Set time of day to session time
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
     * @return PendingIntent to start a Beam background service
     */
    private PendingIntent getPIntentForServiceBroadcast(String time, int service, int command, @Nullable Map<String, String> extras) {
        // Obtain unique request code, unique for each session (but not for each user!)
        int requestCode = Integer.parseInt("1" + time + service + command, 10);
        Log.d("MainFragment", "RequestCode: " + requestCode);

        Intent intent = new Intent(getContext(), BeamBroadcastReceiver.class);
        intent.setAction(BeamBroadcastReceiver.INTENT_ACTION);

        // Pass module details, and service/command codes to broadcasted intent
        intent.putExtra("service", service);
        intent.putExtra("command", command);
        if (extras != null) {
            for (Map.Entry<String, String> entry : extras.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        }
        // FLAG_NO_CREATE will mean that getBroadcast returns ull if broadcast with request code already exists
        if (PendingIntent.getBroadcast(getContext(), requestCode, intent, PendingIntent.FLAG_NO_CREATE) == null) {
            return PendingIntent.getBroadcast(getContext(), requestCode, intent, 0);
        }
        else {
            return null;
        }
    }
}
