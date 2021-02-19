package com.example.beam.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.example.beam.R;
import com.example.beam.models.Session;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleExpandableListAdapter extends BaseExpandableListAdapter {
    private static final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private static List<String> dates;
    private Context context;
    private Map<String, Map<String, Map<String, Session>>> userWeeklyTimetable;
    private Map<String, String> userModules;

    ScheduleExpandableListAdapter(Context context) {
        this.context = context;
        userWeeklyTimetable = new HashMap<>();
        userModules = new HashMap<>();
        dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        for (int i = 0; i < 7; i++) {
            dates.add(String.format("%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)+i));
        }
    }

    @Override
    public int getGroupCount() {
        return days.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (userWeeklyTimetable.containsKey(dates.get(groupPosition))) {
            //Log.d("ScheduleFragment", userWeeklyTimetable.get(dates.get(groupPosition)).toString());
            int num = 0;
            for (Map.Entry<String, Map<String, Session>> entry : userWeeklyTimetable.get(dates.get(groupPosition)).entrySet()) {
                if (entry.getValue() != null) {
                    num += entry.getValue().size();
                }
                else {
                    num += 0;
                }
            }
            return num;
        }
        else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return days[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<Session> sessions = new ArrayList<>();
        for(Map.Entry<String, Map<String, Session>> entry: userWeeklyTimetable.get(dates.get(groupPosition)).entrySet()) {
            sessions.addAll(entry.getValue().values());
        }
        Collections.sort(sessions);
        return sessions.get(childPosition);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.schedule_expandable_parent, viewGroup, false);
        }
        ((TextView) view.findViewById(R.id.schedule_expandable_parent_name)).setText(days[i]);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        List<Session> sessions = new ArrayList<>();
        for(Map.Entry<String, Map<String, Session>> entry: userWeeklyTimetable.get(dates.get(i)).entrySet()) {
            if (entry.getValue() != null) {
                sessions.addAll(entry.getValue().values());
            }
        }
        Collections.sort(sessions);
        Session session = sessions.get(i1);
        Log.d("BeamList", sessions.get(i1).toString());
        if (view == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.schedule_expandable_child, viewGroup, false);
        }
        ((TextView) view.findViewById(R.id.schedule_expandable_child_code)).setText(session.getModuleID());
        ((TextView) view.findViewById(R.id.schedule_expandable_child_name)).setText(userModules.get(session.getModuleID()));
        ((TextView) view.findViewById(R.id.schedule_expandable_child_type)).setText(session.getSessionType());
        String time = session.getTimeBegin() + " - " + session.getTimeEnd();
        ((TextView) view.findViewById(R.id.schedule_expandable_child_time)).setText(time);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_detailed_stats);
            }
        });
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public void setUserWeeklyTimetable(Map<String, Map<String, Map<String, Session>>> userWeeklyTimetable) {
        this.userWeeklyTimetable = userWeeklyTimetable;
    }

    public void setUserModules(Map<String, String> userModules) {
        this.userModules = userModules;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.d("ScheduleFragment", "Dataset changed");
    }
}
