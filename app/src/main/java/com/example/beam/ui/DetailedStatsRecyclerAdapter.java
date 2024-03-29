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
import com.example.beam.models.Session;
import com.example.beam.models.StudentModuleRecord;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * RecyclerViewAdapter subclass used for populating the list in DetailedStatsFragment.
 */
public class DetailedStatsRecyclerAdapter extends RecyclerView.Adapter<DetailedStatsRecyclerAdapter.DetailedStatsRecyclerViewHolder> {
    /** Debug tag */
    private static final String LOG_TAG = "DStatsFragmentAdapter";
    /** Whether user is Student or Lecturer */
    private String userRole;
    /** List of sessions of the specific module */
    private List<Session> userModuleSessions;
    /** List of user attendance history */
    private Record userRecords;

    /** Class for holding each row of the list */
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
    }

    @NonNull
    @Override
    public DetailedStatsRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detailed_stats_recycler_row, parent, false);
        return new DetailedStatsRecyclerViewHolder(view);
    }

    /**
     * Populate row with relevant data
     * @param holder Row container in the list
     * @param position Position in the list
     */
    @Override
    public void onBindViewHolder(@NonNull DetailedStatsRecyclerViewHolder holder, int position) {
        try {
            Session session = userModuleSessions.get(position);
            holder.sessionType.setText(session.getSessionType());
            String time = session.getTime_begin().concat(" - ").concat(session.getTime_end());
            holder.sessionTime.setText(time);

            // Check whether student has attended and display check mark, cross, or nothing
            if (((StudentModuleRecord) userRecords).getAttendance().containsKey(session.getSession_id())) {
                boolean bool = ((StudentModuleRecord) userRecords).getAttendance().get(session.getSession_id());
                if (bool) {
                    holder.status.setImageResource(R.drawable.ic_done);
                    holder.status.setVisibility(View.VISIBLE);
                }
                else {
                    holder.status.setVisibility(View.VISIBLE);
                    holder.status.setImageResource(R.drawable.ic_clear);
                }
            }
            else {
                holder.status.setVisibility(View.INVISIBLE);
                Log.d(LOG_TAG, session.getSession_id() + " : " + userRecords.toString());
            }
        }
        catch (NullPointerException exception) {
            Log.d(LOG_TAG, exception.toString());
        }
    }

    /**
     * Get number of sessions
     * @return Size of list, number of sessions in module
     */
    @Override
    public int getItemCount() {
        try {
            if (userRole.equals("Student")) {
                return userModuleSessions.size();
                /*
                StudentModuleRecord studentRecord = new StudentModuleRecord();
                for (Record record : userRecords) {
                    if (record.getModuleID().equals(moduleCode)) {
                        studentRecord = ((StudentModuleRecord) record);
                        break;
                    }
                }
                Log.d(LOG_TAG, studentRecord.toString());
                return studentRecord.getAttendance().values().size();

                 */
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

    /**
     * Sort list based on session ID.
     * @param userModuleSessions List of sessions of the specific module
     */
    public void setUserModuleSessions(List<Session> userModuleSessions) {
        this.userModuleSessions = userModuleSessions;
        Collections.sort(this.userModuleSessions, new Comparator<Session>() {
            @Override
            public int compare(Session session, Session t1) {
                return session.getSession_id().compareTo(t1.getSession_id());
            }
        });
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public void setUserRecords(Record userRecords) {
        this.userRecords = userRecords;
    }
}
