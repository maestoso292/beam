package com.example.beam.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beam.R;
import com.example.beam.models.Session;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TodayRecyclerAdapter extends RecyclerView.Adapter<TodayRecyclerAdapter.TodayRecyclerViewHolder> {
    private List<Session> userTodayTimetable;

    public static class TodayRecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView status;
        TextView moduleName;
        TextView sessionTime;

        public TodayRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            status = itemView.findViewById(R.id.today_recycler_row_status);
            moduleName = itemView.findViewById(R.id.today_recycler_row_module_name);
            sessionTime = itemView.findViewById(R.id.today_recycler_row_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view).navigate(R.id.action_detailed_stats);
                }
            });
        }
    }

    public void setUserTodayTimetable(List<Session> userTodayTimetable) {
        this.userTodayTimetable = userTodayTimetable;
    }

    @NonNull
    @Override
    public TodayRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.today_recycler_row, parent,false);
        return new TodayRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodayRecyclerViewHolder holder, int position) {
        holder.status.setImageResource(ThreadLocalRandom.current().nextInt(0, 2) == 0 ? R.drawable.ic_done : R.drawable.ic_clear);
        holder.moduleName.setText("Sample Module Name");
        holder.sessionTime.setText("9:00 - 11:00");
    }

    @Override
    public int getItemCount() {
        return userTodayTimetable.size();
    }
}
