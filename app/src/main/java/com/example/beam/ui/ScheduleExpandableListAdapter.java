package com.example.beam.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.example.beam.DaySchedule;
import com.example.beam.R;
import com.example.beam.Session;

import java.util.HashMap;
import java.util.Map;

public class ScheduleExpandableListAdapter extends BaseExpandableListAdapter {
    private static final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private Context context;
    private Map<String, DaySchedule> dayScheduleMap;
    private Map<String, String> userModules;

    ScheduleExpandableListAdapter(Context context) {
        this.context = context;
        dayScheduleMap = new HashMap<>();
        userModules = new HashMap<>();
    }

    @Override
    public int getGroupCount() {
        return days.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (dayScheduleMap.containsKey(days[groupPosition].toLowerCase())) {
            return dayScheduleMap.get(days[groupPosition].toLowerCase()).getNumSessions();
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
        return dayScheduleMap.get(days[groupPosition].toLowerCase()).daySessions.get(childPosition);
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
        Session session = dayScheduleMap.get(days[i].toLowerCase()).daySessions.get(i1);
        if (view == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.schedule_expandable_child, viewGroup, false);
        }
        ((TextView) view.findViewById(R.id.schedule_expandable_child_code)).setText(session.moduleCode);
        ((TextView) view.findViewById(R.id.schedule_expandable_child_name)).setText(userModules.containsKey(session.moduleCode) ? userModules.get(session.moduleCode) : "Sample");
        ((TextView) view.findViewById(R.id.schedule_expandable_child_type)).setText(session.sessionType);
        String time = session.timeBegin + " - " + session.timeEnd;
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

    public void setDayScheduleMap(Map<String, DaySchedule> dayScheduleMap) {
        this.dayScheduleMap = dayScheduleMap;
    }

    public void setUserModules(Map<String, String> userModules) {
        this.userModules = userModules;
    }
}
