package com.example.beam.ui;

import android.os.Bundle;
import android.util.Log;
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

public class DetailedStatsFragment extends Fragment {
    private static final String LOG_TAG = "DStatsFragment";

    private String moduleCode;
    private String userRole;

    private RecyclerView recyclerView;
    private DetailedStatsRecyclerAdapter recyclerAdapter;

    private BeamViewModel beamViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.detailed_stats_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        moduleCode = DetailedStatsFragmentArgs.fromBundle(getArguments()).getModuleCode();
        Log.d(LOG_TAG, "ModuleCode:" + moduleCode);
        beamViewModel = new ViewModelProvider(getActivity()).get(BeamViewModel.class);
        beamViewModel.getUserDetails().observe(getViewLifecycleOwner(), new Observer<BeamUser>() {
            @Override
            public void onChanged(BeamUser beamUser) {
                userRole = beamUser.getRole();
            }
        });

        recyclerView = view.findViewById(R.id.detailed_stats_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerAdapter = new DetailedStatsRecyclerAdapter();
        recyclerView.setAdapter(recyclerAdapter);
    }
}
