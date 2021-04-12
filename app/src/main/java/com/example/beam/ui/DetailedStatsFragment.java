package com.example.beam.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.example.beam.models.Session;

import java.util.List;

public class DetailedStatsFragment extends Fragment {
    private static final String LOG_TAG = "DStatsFragment";

    private String moduleCode;
    private String moduleName;
    private String userRole;

    private TextView percentage;
    private TextView numAttended;
    private TextView numTotal;

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

        percentage = view.findViewById(R.id.detailed_stats_percentage);
        numAttended = view.findViewById(R.id.detailed_stats_attended);
        numTotal = view.findViewById(R.id.detailed_stats_total);

        moduleCode = DetailedStatsFragmentArgs.fromBundle(getArguments()).getModuleCode();
        moduleName = DetailedStatsFragmentArgs.fromBundle(getArguments()).getModuleName();

        TextView moduleNameTextView = view.findViewById(R.id.detailed_stats_module_name);
        moduleNameTextView.setText(moduleName);

        recyclerView = view.findViewById(R.id.detailed_stats_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerAdapter = new DetailedStatsRecyclerAdapter();
        recyclerView.setAdapter(recyclerAdapter);

        Log.d(LOG_TAG, "ModuleCode:" + moduleCode);

        recyclerAdapter.setModuleCode(moduleCode);

        beamViewModel = new ViewModelProvider(getActivity()).get(BeamViewModel.class);
        beamViewModel.getUserDetails().observe(getViewLifecycleOwner(), new Observer<BeamUser>() {
            @Override
            public void onChanged(BeamUser beamUser) {
                userRole = beamUser.getRole();
                recyclerAdapter.setUserRole(userRole);
                recyclerAdapter.notifyDataSetChanged();
            }
        });

        beamViewModel.getUserRecord().observe(getViewLifecycleOwner(), new Observer<List<? extends Record>>() {
            @Override
            public void onChanged(List<? extends Record> records) {
                for (Record record : records) {
                    if (record.getModuleID().equals(moduleCode)) {

                        numTotal.setText("" + record.getNumTotal());
                        numAttended.setText("" + record.getNumAttended());
                        percentage.setText(record.getPercentageString());

                        recyclerAdapter.setUserRecords(record);
                        recyclerAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        });

        beamViewModel.getUserModuleSessions(moduleCode).observe(getViewLifecycleOwner(), new Observer<List<Session>>() {
            @Override
            public void onChanged(List<Session> sessions) {
                recyclerAdapter.setUserModuleSessions(sessions);
                recyclerAdapter.notifyDataSetChanged();
            }
        });
    }




}
