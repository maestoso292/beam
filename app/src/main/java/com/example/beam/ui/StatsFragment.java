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
import com.example.beam.models.StudentModuleRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Map;

public class StatsFragment extends Fragment {
    private static final String LOG_TAG = "StatsFragment";

    RecyclerView recyclerView;
    StatsRecyclerAdapter recyclerAdapter;
    BeamViewModel beamViewModel;

    private FirebaseUser currentUser;
    private String userRole;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stats_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

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

    private void loadRecords() {
        if (userRole.equals("Student")) {
            beamViewModel.getUserModules().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
                @Override
                public void onChanged(Map<String, String> userModules) {
                    recyclerAdapter.setUserModules(userModules);
                }
            });
            beamViewModel.getStudentRecord().observe(getViewLifecycleOwner(), new Observer<List<StudentModuleRecord>>() {
                @Override
                public void onChanged(List<StudentModuleRecord> studentModuleRecords) {
                    recyclerAdapter.setUserModuleRecords(studentModuleRecords);
                }
            });
        }
        else if (userRole.equals("Lecturer")) {

        }
        else {
            Log.d(LOG_TAG, "Invalid User Role");
        }
    }
}
