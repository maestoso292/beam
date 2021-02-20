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

import java.util.HashMap;
import java.util.Map;

public class ScheduleFragment extends Fragment {
    private ExpandableListView expandableListView;
    private ScheduleExpandableListAdapter expandableListAdapter;
    private BeamViewModel beamViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.schedule_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        expandableListView = view.findViewById(R.id.schedule_expandable);
        expandableListAdapter = new ScheduleExpandableListAdapter(getContext());
        expandableListAdapter.setUserModules(new HashMap<String, String>());
        expandableListView.setAdapter(expandableListAdapter);

        beamViewModel = new ViewModelProvider(getActivity()).get(BeamViewModel.class);
        beamViewModel.getUserWeeklyTimetable().observe(getViewLifecycleOwner(), new Observer<TimeTable>() {
            @Override
            public void onChanged(TimeTable timeTable) {
                expandableListAdapter.setUserWeeklyTimetable(timeTable);
            }
        });
        beamViewModel.getUserModules().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> userModules) {
                expandableListAdapter.setUserModules(userModules);
            }
        });

    }
}
