package com.example.beam.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beam.BeamViewModel;
import com.example.beam.R;
import com.example.beam.models.BeamUser;
import com.example.beam.models.Record;

import java.util.List;
import java.util.Map;

/**
 * Fragment subclass to display general attendance statistics (using RecyclerView) of each module
 * user is enrolled in or teaches.
 */
public class StatsFragment extends Fragment {
    private static final String LOG_TAG = "StatsFragment";

    RecyclerView recyclerView;
    StatsRecyclerAdapter recyclerAdapter;
    BeamViewModel beamViewModel;

    private String userRole;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stats_fragment, container, false);
    }

    /**
     * On view created, fetch relevant data and pass to RecyclerView adapter to populate each row
     * @param view XML view of the Fragment
     * @param savedInstanceState Previous saved state of the Fragment
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.stats_recycler);
        recyclerAdapter = new StatsRecyclerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerAdapter);

        beamViewModel = new ViewModelProvider(getActivity()).get(BeamViewModel.class);
        beamViewModel.getUserDetails().observe(getViewLifecycleOwner(), new Observer<BeamUser>() {
            @Override
            public void onChanged(BeamUser beamUser) {
                userRole = beamUser.getRole();
                recyclerAdapter.setUserRole(userRole);
                loadRecords();
            }
        });

    }

    // Load attendance history
    private void loadRecords() {
        beamViewModel.getUserModules().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> userModules) {
                recyclerAdapter.setUserModules(userModules);
            }
        });
        beamViewModel.getUserRecord().observe(getViewLifecycleOwner(), new Observer<List<? extends Record>>() {
            @Override
            public void onChanged(List<? extends Record> userModuleRecords) {
                recyclerAdapter.setUserModuleRecords(userModuleRecords);
            }
        });
    }
}
