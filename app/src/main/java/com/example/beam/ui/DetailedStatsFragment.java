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

/**
 * Class for implementing Detailed Statistics Screen where users can view attendance history
 * of specific module.
 */
public class DetailedStatsFragment extends Fragment {
    /** Debug tag */
    private static final String LOG_TAG = "DStatsFragment";
    /** Module ID of specific module */
    private String moduleCode;
    /** Module name of specific module */
    private String moduleName;
    /** Whether user is Student or Lecturer */
    private String userRole;

    /** XML view of attendance percentage */
    private TextView percentage;
    /** XML view of number of sessions attended */
    private TextView numAttended;
    /** XML view of number of total sessions */
    private TextView numTotal;
    /** RecyclerView instance that displays a list for the attendance history */
    private RecyclerView recyclerView;
    /** Adapter to populate each row of the list */
    private DetailedStatsRecyclerAdapter recyclerAdapter;
    /** ViewModel that handles fetching relevant data from the database */
    private BeamViewModel beamViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate corresponding layout in res/layout
        return inflater.inflate(R.layout.detailed_stats_fragment, container, false);
    }

    /**
     * After view is created, fetch relevant data and pass to recycler adapter and sets
     * relevant values in the XML view.
     * @param view XML view containing the entire fragment
     * @param savedInstanceState Previous saved instance of this fragment
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Find instance of XML views
        percentage = view.findViewById(R.id.detailed_stats_percentage);
        numAttended = view.findViewById(R.id.detailed_stats_attended);
        numTotal = view.findViewById(R.id.detailed_stats_total);
        // Find module details passed as arguments on navigation
        moduleCode = DetailedStatsFragmentArgs.fromBundle(getArguments()).getModuleCode();
        moduleName = DetailedStatsFragmentArgs.fromBundle(getArguments()).getModuleName();

        TextView moduleNameTextView = view.findViewById(R.id.detailed_stats_module_name);
        moduleNameTextView.setText(moduleName);

        // Find RecyclerView instance and set its layout manager and adapter
        recyclerView = view.findViewById(R.id.detailed_stats_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerAdapter = new DetailedStatsRecyclerAdapter();
        recyclerView.setAdapter(recyclerAdapter);

        Log.d(LOG_TAG, "ModuleCode:" + moduleCode);

        // Fetch relevant data from BeamViewModel instance and pass to adapter
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
