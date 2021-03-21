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
import com.example.beam.models.TimeTable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class TodayFragment extends Fragment {
    private static final String LOG_TAG = "TodayFragment";

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
        recyclerViewAdapter.setUserDailyTimetable(new ArrayList<>());
        recyclerViewAdapter.setUserModules(new HashMap<>());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        final String date = String.format(Locale.ENGLISH, "%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
        Log.d(LOG_TAG, "Date today: " + date);
        beamViewModel = new ViewModelProvider(getActivity()).get(BeamViewModel.class);
        beamViewModel.getUserDetails().observe(getViewLifecycleOwner(), new Observer<BeamUser>() {
            @Override
            public void onChanged(BeamUser beamUser) {
                beamViewModel.getUserModules().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
                    @Override
                    public void onChanged(Map<String, String> userModules) {
                        recyclerViewAdapter.setUserModules(userModules);
                    }
                });
                beamViewModel.getUserWeeklyTimetable().observe(getViewLifecycleOwner(), new Observer<TimeTable>() {
                    @Override
                    public void onChanged(TimeTable timeTable) {
                        try {
                            recyclerViewAdapter.setUserDailyTimetable(timeTable.getDailyTimetable(date));
                            Log.d(LOG_TAG, timeTable.getDailyTimetable(date).toString());
                        }
                        catch (NullPointerException exception) {
                            Log.d(LOG_TAG, "No daily sessions for " + date + ": " + exception);
                            Log.d(LOG_TAG, "Weekly Sessions: " + timeTable.getWeeklyTimetable());
                        }
                    }
                });
            }
        });
    }
}
