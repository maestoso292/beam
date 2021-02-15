package com.example.beam.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beam.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StatsRecyclerAdapter extends RecyclerView.Adapter<StatsRecyclerAdapter.StatsRecyclerViewHolder> {
    private List<String> userModuleCodes;
    private Map<String, String> userModules;

    public static class StatsRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView moduleCode;
        TextView moduleName;

        public StatsRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            moduleCode = itemView.findViewById(R.id.stats_recycler_row_module_code);
            moduleName = itemView.findViewById(R.id.stats_recycler_row_module_name);

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
        String moduleCode = userModuleCodes.get(position);
        holder.moduleCode.setText(moduleCode);
        holder.moduleName.setText(userModules.get(moduleCode));
    }

    @Override
    public int getItemCount() {
        return userModules.size();
    }

    public void setUserModules(Map<String, String> userModules) {
        this.userModules = userModules;
        userModuleCodes = new ArrayList<>(userModules.keySet());
        Collections.sort(userModuleCodes);
    }
}
