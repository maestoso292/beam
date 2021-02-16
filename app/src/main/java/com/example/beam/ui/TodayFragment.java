package com.example.beam.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beam.BeamViewModel;
import com.example.beam.R;
import com.example.beam.models.Session;

import java.util.ArrayList;
import java.util.Calendar;

public class TodayFragment extends Fragment {
    private RecyclerView recyclerView;
    private TodayRecyclerAdapter recyclerViewAdapter;

    private BeamViewModel beamViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.today_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.today_recycler);
        recyclerViewAdapter = new TodayRecyclerAdapter();
        recyclerViewAdapter.setUserTodayTimetable(new ArrayList<Session>());
        //recyclerViewAdapter.setUserModules(new HashMap<String, String>());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Calendar calendar = Calendar.getInstance();
        final String date = String.format("%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        beamViewModel = new ViewModelProvider(getActivity()).get(BeamViewModel.class);
        /*
        beamViewModel.getUserModules().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> userModules) {
                recyclerViewAdapter.setUserModules(userModules);
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });

         */
        /*
        beamViewModel.getUserWeeklyTimetable().observe(getViewLifecycleOwner(), new Observer<Map<String, Map<String, Map<String, Session>>>>() {
            @Override
            public void onChanged(Map<String, Map<String, Map<String, Session>>> userWeeklyTimetable) {
                if (userWeeklyTimetable.containsKey(date)) {
                    List<Session> sessions = new ArrayList<>();

                    for(Map.Entry<String, Map<String, Session>> entry: userWeeklyTimetable.get(date).entrySet()) {
                        if (entry.getValue() != null) {
                            sessions.addAll(entry.getValue().values());
                        }
                    }
                    Collections.sort(sessions);
                    recyclerViewAdapter.setUserTodayTimetable(sessions);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });

         */
    }
}
