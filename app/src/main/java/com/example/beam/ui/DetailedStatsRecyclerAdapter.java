package com.example.beam.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beam.R;
import com.example.beam.models.Record;
import com.example.beam.models.StudentModuleRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DetailedStatsRecyclerAdapter extends RecyclerView.Adapter<DetailedStatsRecyclerAdapter.DetailedStatsRecyclerViewHolder> {
    private static final String LOG_TAG = "DStatsFragmentAdapter";

    private String userRole;
    private String moduleCode;
    private List<? extends Record> userRecords;

    public static class DetailedStatsRecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView status;
        TextView sessionType;
        TextView sessionTime;

        public DetailedStatsRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            status = itemView.findViewById(R.id.detailed_stats_recycler_row_status);
            sessionType = itemView.findViewById(R.id.detailed_stats_recycler_row_type);
            sessionTime = itemView.findViewById(R.id.detailed_stats_recycler_row_time);
        }
    }

    public DetailedStatsRecyclerAdapter() {
        userRecords = new ArrayList<>();
    }

    @NonNull
    @Override
    public DetailedStatsRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detailed_stats_recycler_row, parent, false);
        return new DetailedStatsRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailedStatsRecyclerViewHolder holder, int position) {
        holder.status.setImageResource(ThreadLocalRandom.current().nextInt(0, 2) == 0 ? R.drawable.ic_done : R.drawable.ic_clear);
        holder.sessionType.setText("Sample Module Name");
        holder.sessionTime.setText("9:00 - 11:00");
    }

    @Override
    public int getItemCount() {
        try {
            if (userRole.equals("Student")) {
                StudentModuleRecord studentRecord = new StudentModuleRecord();
                for (Record record : userRecords) {
                    if (record.getModuleID().equals(moduleCode)) {
                        studentRecord = ((StudentModuleRecord) record);
                        break;
                    }
                }
                Log.d(LOG_TAG, studentRecord.toString());
                return studentRecord.getAttendance().values().size();
            }
            else if (userRole.equals("Lecturer")) {
                // TODO Implement getting module records size for lecturer
            }
        }
        catch (NullPointerException exception) {
            Log.d(LOG_TAG, "Error getting session count: " + exception);
        }
        return 0;

    }
}
