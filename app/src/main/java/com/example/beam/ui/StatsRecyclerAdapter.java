package com.example.beam.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beam.R;
import com.example.beam.models.Record;
import com.example.beam.models.StudentModuleRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsRecyclerAdapter extends RecyclerView.Adapter<StatsRecyclerAdapter.StatsRecyclerViewHolder> {
    private static final String LOG_TAG = "StatsFragmentAdapter";

    private String userRole;
    private List<String> userModuleCodes;
    private Map<String, String> userModules;
    private List<? extends Record> userModuleRecords;
    private Map<String, Double> userModuleStats;

    public static class StatsRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView moduleCode;
        TextView moduleName;
        TextView modulePercentage;

        public StatsRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            moduleCode = itemView.findViewById(R.id.stats_recycler_row_module_code);
            moduleName = itemView.findViewById(R.id.stats_recycler_row_module_name);
            modulePercentage = itemView.findViewById(R.id.stats_recycler_row_module_percentage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainFragmentDirections.ActionDetailedStats action = MainFragmentDirections.actionDetailedStats();
                    action.setModuleCode(moduleCode.getText().toString());
                    action.setModuleName(moduleName.getText().toString());
                    Navigation.findNavController(view).navigate(action);
                }
            });
        }
    }

    public StatsRecyclerAdapter() {
        userModuleCodes = new ArrayList<>();
        userModules = new HashMap<>();
        userModuleRecords = new ArrayList<>();
        userModuleStats = new HashMap<>();
    }

    @NonNull
    @Override
    public StatsRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stats_recycler_row, parent, false);
        return new StatsRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatsRecyclerViewHolder holder, int position) {
        String moduleCode = userModuleCodes.get(position);
        holder.moduleCode.setText(moduleCode);
        holder.moduleName.setText(userModules.get(moduleCode));
        double avg = userModuleStats.get(moduleCode).doubleValue();
        String avgString;
        if (avg >= 0) {
            avgString = String.format("%d%%", Math.round(avg * 100));
        }
        else {
            avgString = "N/A";
        }
        holder.modulePercentage.setText(avgString);
    }

    @Override
    public int getItemCount() {
        return userModules.size();
    }

    private void calculateStudentModuleStats() {
        for (Record record : userModuleRecords) {
            StudentModuleRecord temp = ((StudentModuleRecord) record);
            double avg = -1;
            try {
                int numAttended = 0;
                List<Boolean> list = new ArrayList<>(temp.getAttendance().values());
                for (Boolean bool : list) {
                    if (bool) {
                        numAttended++;
                    }
                }
                avg = ((double) numAttended) / list.size();
            }
            catch (NullPointerException exception) {
                Log.d(LOG_TAG, "No record for " + temp.getModuleID() + ": " + exception);
            }
            finally {
                userModuleStats.put(temp.getModuleID(), avg);
            }

        }
        Log.d(LOG_TAG, userModuleStats.toString());
    }

    private void calculateLecturerModuleStats() {
        // TODO Implement to calculate average attendance
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public void setUserModules(Map<String, String> userModules) {
        this.userModules = userModules;
        userModuleCodes = new ArrayList<>(userModules.keySet());
        Collections.sort(userModuleCodes);
        notifyDataSetChanged();
    }

    public void setUserModuleRecords(List<? extends Record> userModuleRecords) {
        this.userModuleRecords = userModuleRecords;
        if (userRole.equals("Student")) {
            calculateStudentModuleStats();
        }
        else if (userRole.equals("Lecturer")) {
            calculateLecturerModuleStats();
        }
        else {
            Log.d(LOG_TAG, "Invalid User Role");
        }
        notifyDataSetChanged();
    }
}
