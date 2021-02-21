package com.example.beam.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.beam.BeamViewModel;
import com.example.beam.R;
import com.example.beam.models.TimeTable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ScheduleFragment extends Fragment {
    private ExpandableListView expandableListView;
    private ScheduleExpandableListAdapter expandableListAdapter;
    private BeamViewModel beamViewModel;
    private DatabaseReference mDatabase;
    private Map<String, String> modules;
    private TimeTable timeTable;

    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.schedule_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Button button = view.findViewById(R.id.schedule_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                for (int i = 20210215; i < 20210221; i++) {

                    List<Session> sessions = timeTable.getDailyTimetable(Integer.toString(i, 10));
                    for (Session session : sessions) {
                        boolean bool = ThreadLocalRandom.current().nextBoolean();
                        mDatabase.child("student_record").child(currentUser.getUid()).child(session.getModuleID()).child(session.getSessionID()).setValue(bool);
                        mDatabase.child("record").child(session.getModuleID()).child(session.getSessionID()).child(currentUser.getUid()).setValue(bool);
                    }
                }
                   */
                /*
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(2021,1,15);

                        Calendar end = Calendar.getInstance();
                        end.set(2021,4,10);
                        while(calendar.compareTo(end) < 0) {
                            final String date = String.format("%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH));
                            mDatabase.child("timetable").child(date).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    GenericTypeIndicator<Map<String, Map<String, Session>>> t = new GenericTypeIndicator<Map<String, Map<String, Session>>>() {};
                                    Map<String, Map<String, Session>> temp = snapshot.getValue(t);
                                    try {
                                        Log.d("ScheduleFrag", temp.toString());
                                        for (Map.Entry<String, Map<String, Session>> entry : temp.entrySet()) {
                                            for (Map.Entry<String, Session> entry1 : entry.getValue().entrySet()) {
                                                mDatabase.child("modules_session").child(entry1.getValue().getModuleID()).child(entry1.getValue().getSessionID()).setValue(entry1.getValue());
                                            }
                                        }
                                    }
                                    catch (NullPointerException e) {
                                        Log.d("ScheduleFrag ",date + " " + e.toString());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            calendar.add(Calendar.DATE, 1);
                        }
                    }
                }).start();
                */

            }
        });


        expandableListView = view.findViewById(R.id.schedule_expandable);
        expandableListAdapter = new ScheduleExpandableListAdapter(getContext());
        expandableListAdapter.setUserModules(new HashMap<String, String>());
        expandableListView.setAdapter(expandableListAdapter);

        beamViewModel = new ViewModelProvider(getActivity()).get(BeamViewModel.class);
        beamViewModel.getUserWeeklyTimetable().observe(getViewLifecycleOwner(), new Observer<TimeTable>() {
            @Override
            public void onChanged(TimeTable userWeeklyTimetable) {
                expandableListAdapter.setUserWeeklyTimetable(userWeeklyTimetable);
                timeTable = userWeeklyTimetable;
            }
        });
        beamViewModel.getUserModules().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> userModules) {
                expandableListAdapter.setUserModules(userModules);
                modules = userModules;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }
}
