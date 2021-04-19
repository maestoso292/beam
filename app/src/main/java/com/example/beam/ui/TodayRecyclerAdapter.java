package com.example.beam.ui;

import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodayRecyclerAdapter extends RecyclerView.Adapter<TodayRecyclerAdapter.TodayRecyclerViewHolder> {
    private static final String LOG_TAG = "TodayFragmentAdapter";

    private List<Session> userDailyTimetable;
    private Map<String, String> userModules;

    public TodayRecyclerAdapter() {
        userDailyTimetable = new ArrayList<>();
        userModules = new HashMap<>();
    }


    public static class TodayRecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView status;
        TextView moduleName;
        TextView moduleCode;
        TextView sessionType;
        TextView sessionTime;

        public TodayRecyclerViewHolder(@NonNull final View itemView) {
            super(itemView);

            status = itemView.findViewById(R.id.today_recycler_row_status);
            moduleName = itemView.findViewById(R.id.today_recycler_row_module_name);
            moduleCode = itemView.findViewById(R.id.today_recycler_row_module_code);
            sessionType = itemView.findViewById(R.id.today_recycler_row_session_type);
            sessionTime = itemView.findViewById(R.id.today_recycler_row_time);

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

    public void setUserDailyTimetable(List<Session> userDailyTimetable) {
        this.userDailyTimetable = userDailyTimetable;
        notifyDataSetChanged();
    }

    public void setUserModules(Map<String, String> userModules) {
        this.userModules = userModules;
        notifyDataSetChanged();
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
        //holder.status.setImageResource(ThreadLocalRandom.current().nextInt(0, 2) == 0 ? R.drawable.ic_done : R.drawable.ic_clear);
        Session currentSession = userDailyTimetable.get(position);
        holder.moduleCode.setText(currentSession.getModule_id());
        holder.moduleName.setText(userModules.get(currentSession.getModule_id()));
        holder.sessionType.setText(currentSession.getSessionType());
        String time = "" + currentSession.getTime_begin() + " - " + currentSession.getTime_end();
        holder.sessionTime.setText(time);
    }


    @Override
    public int getItemCount() {
        try {
            return userDailyTimetable.size();
        }
        catch (NullPointerException exception) {
            return 0;
        }
        catch (Exception exception) {
            Log.d(LOG_TAG, "Exception: " + exception);
            return 0;
        }
    }
}
