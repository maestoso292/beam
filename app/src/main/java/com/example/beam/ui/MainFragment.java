package com.example.beam.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.beam.BeamViewModel;
import com.example.beam.R;
import com.example.beam.models.BeamUser;
import com.example.beam.models.Session;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
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


        pager = view.findViewById(R.id.main_pager);
        //tabLayout = view.findViewById(R.id.main_tab_layout);
        adapter = new MainFragmentAdapter(this);

        pager.setAdapter(adapter);


        /*
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(adapter.getTabHeadings().get(position));
            }
        }
        );
        tabLayoutMediator.attach();

         */
    }

    @Override
    public void onResume() {
        super.onResume();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            navController.navigate(R.id.signin_fragment);
        }
        else {
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
