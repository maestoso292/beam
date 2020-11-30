package com.example.beam.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beam.R;

public class StatsRecyclerAdapter extends RecyclerView.Adapter<StatsRecyclerAdapter.StatsRecyclerViewHolder> {
    public static class StatsRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView moduleName;
        TextView modulePercentage;

        public StatsRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            moduleName = itemView.findViewById(R.id.stats_recycler_row_module_name);
            modulePercentage = itemView.findViewById(R.id.stats_recycler_row_module_percentage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view).navigate(R.id.action_detailed_stats);
                }
            });
        }
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
        holder.moduleName.setText("Sample Module Name");
        holder.modulePercentage.setText("Sample Module Percentage");
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
