package com.example.beam.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beam.R;

import java.util.concurrent.ThreadLocalRandom;

public class DetailedStatsRecyclerAdapter extends RecyclerView.Adapter<DetailedStatsRecyclerAdapter.DetailedStatsRecyclerViewHolder> {
    public static class DetailedStatsRecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView status;
        TextView moduleName;
        TextView sessionTime;

        public DetailedStatsRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            status = itemView.findViewById(R.id.detailed_stats_recycler_row_status);
            moduleName = itemView.findViewById(R.id.detailed_stats_recycler_row_module_name);
            sessionTime = itemView.findViewById(R.id.detailed_stats_recycler_row_time);
        }
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
        holder.moduleName.setText("Sample Module Name");
        holder.sessionTime.setText("9:00 - 11:00");
    }

    @Override
    public int getItemCount() {
        return 10;
    }
}
