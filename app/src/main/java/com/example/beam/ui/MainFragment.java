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

public class MainFragment extends Fragment {
    private ViewPager2 pager;
    private TabLayout tabLayout;
    private MainFragmentAdapter adapter;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private NavController navController;
    private BeamViewModel beamViewModel;

    private AlarmManager alarmManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);

        mAuth = FirebaseAuth.getInstance();

        beamViewModel = new ViewModelProvider(getActivity()).get(BeamViewModel.class);
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

        currentUser = mAuth.getCurrentUser();
        if (beamViewModel.isFirstLoad() || currentUser == null) {
            beamViewModel.setFirstLoad(false);
            navController.navigate(R.id.splashFragment);
        }

        pager.setCurrentItem(0);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        final String date = String.format(Locale.ENGLISH, "%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
        final String currentTime = String.format(Locale.ENGLISH, "%02d%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        beamViewModel.getUserWeeklyTimetable().observe(getViewLifecycleOwner(), new Observer<TimeTable>() {
            @Override
            public void onChanged(TimeTable timeTable) {
                Log.d("MainFragment", date + ": " + timeTable.getWeeklyTimetable().size());

                if (timeTable.getWeeklyTimetable().size() != 7) {
                    Log.d("MainFragment", "timetable not large enough: " + timeTable.getWeeklyTimetable().size());
                    return;
                }

                try {
                    List<Session> sessions = timeTable.getDailyTimetable(date);
                    Log.d("MainFragment", "Sessions Size: " + sessions.size());
                    for (Session session : sessions) {
                        if (currentTime.compareTo(session.getTime_begin()) > 0) {
                            Log.d("MainFragment", "Current time > session time");
                            continue;
                        }

                        long sessionBeginMillisecond = getMillisecondForSessionTime(session.getTime_begin());
                        long sessionEndMillisecond = getMillisecondForSessionTime(session.getTime_end());

                        Map<String, String> extras = new HashMap<>();
                        extras.put("moduleId", session.getModule_id());
                        extras.put("sessionId", session.getSession_id());

                        PendingIntent startPIntent;
                        PendingIntent stopPIntent;
                        String userRole = beamViewModel.getUserDetails().getValue().getRole();
                        if (userRole.equals("Student")) {
                            startPIntent = getPIntentForServiceBroadcast(session.getTime_begin(), BeamBroadcastReceiver.CENTRAL_SERVICE, BeamBroadcastReceiver.START_SERVICE, extras);
                            stopPIntent = getPIntentForServiceBroadcast(session.getTime_end(), BeamBroadcastReceiver.CENTRAL_SERVICE, BeamBroadcastReceiver.STOP_SERVICE, null);
                        }
                        else if (userRole.equals("Lecturer")) {
                            startPIntent = getPIntentForServiceBroadcast(session.getTime_begin(), BeamBroadcastReceiver.PERIPHERAL_SERVICE, BeamBroadcastReceiver.START_SERVICE, extras);
                            stopPIntent = getPIntentForServiceBroadcast(session.getTime_end(), BeamBroadcastReceiver.PERIPHERAL_SERVICE, BeamBroadcastReceiver.STOP_SERVICE, null);
                        }
                        else {
                            Log.d("MainFragment", "No user role.");
                            Toast.makeText(getContext(), "No user role", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (startPIntent==null) {
                            Log.d("MainFragment", "Start Intent Already Exists");
                            continue;
                        }
                        if (stopPIntent==null) {
                            Log.d("MainFragment", "Stop Intent Already Exists");
                            continue;
                        }

                        Log.d("MainFragment", "Service Alarm posted: " + sessionBeginMillisecond);
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
     * @return PendingIntent to start a Beam background service
     */
    private PendingIntent getPIntentForServiceBroadcast(String time, int service, int command, @Nullable Map<String, String> extras) {
        int requestCode = Integer.parseInt("1" + time + service + command, 10);
        Log.d("MainFragment", "RequestCode: " + requestCode);

        Intent intent = new Intent(getContext(), BeamBroadcastReceiver.class);
        intent.setAction(BeamBroadcastReceiver.INTENT_ACTION);

        intent.putExtra("service", service);
        intent.putExtra("command", command);
        if (extras != null) {
            for (Map.Entry<String, String> entry : extras.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        }
        if (PendingIntent.getBroadcast(getContext(), requestCode, intent, PendingIntent.FLAG_NO_CREATE) == null) {
            return PendingIntent.getBroadcast(getContext(), requestCode, intent, 0);
        }
        else {
            return null;
        }
    }
}
