package com.example.beam.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Fragment subclass to display weekly schedule using an ExpandableListView.
 */
public class ScheduleFragment extends Fragment {
    /** Multi-level list. Level 1: Day of week. Level 2: Sessions in the day */
    private ExpandableListView expandableListView;
    /** Adapter for multi-level list. Populates the list */
    private ScheduleExpandableListAdapter expandableListAdapter;
    /** BeamViewModel instance to obtain relevant data */
    private BeamViewModel beamViewModel;
    /** Reference to database root */
    private DatabaseReference mDatabase;
    /** Map of module ID to Name containing modules user teaches or is enrolled in. */
    private Map<String, String> modules;
    /** Weekly timetable instance */
    private TimeTable timeTable;
    /** Current authentication state */
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.schedule_fragment, container, false);
    }

    /**
     * Instantiates multi-level list, fetch relevant data and pass to corresponding adapter.
     * @param view XML view of Fragment.
     * @param savedInstanceState Previous saved instance of the Fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        expandableListView = view.findViewById(R.id.schedule_expandable);
        expandableListAdapter = new ScheduleExpandableListAdapter(getContext());
        expandableListAdapter.setUserModules(new HashMap<>());
        expandableListView.setAdapter(expandableListAdapter);

        // Obtain weekly timetable and pass to list adapter
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
