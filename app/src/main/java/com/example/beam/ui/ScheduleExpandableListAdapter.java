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
import com.example.beam.models.TimeTable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ScheduleExpandableListAdapter extends BaseExpandableListAdapter {
    private static final String LOG_TAG = "ScheduleFragmentAdapter";
    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    private final List<String> dates;
    private final Context context;

    private TimeTable userWeeklyTimetable;
    private Map<String, String> userModules;

    ScheduleExpandableListAdapter(Context context) {
        this.context = context;
        userWeeklyTimetable = new TimeTable();
        userModules = new HashMap<>();

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        int calendarOffset = 0;
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.TUESDAY:
                calendarOffset = 1;
                break;
            case Calendar.WEDNESDAY:
                calendarOffset = 2;
                break;
            case Calendar.THURSDAY:
                calendarOffset = 3;
                break;
            case Calendar.FRIDAY:
                calendarOffset = 4;
                break;
            case Calendar.SATURDAY:
                calendarOffset = 5;
                break;
            case Calendar.SUNDAY:
                calendarOffset = 6;
                break;
        }
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - calendarOffset);

        dates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            dates.add(String.format(Locale.ENGLISH,"%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH)+i));
        }
    }

    @Override
    public int getGroupCount() {
        return DAYS.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        try {
            return userWeeklyTimetable.getDailyTimetable(dates.get(groupPosition)).size();
        }
        catch (NullPointerException exception) {
            return  0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return DAYS[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<Session> sessions;
        try {
             sessions = userWeeklyTimetable.getDailyTimetable(dates.get(groupPosition));
             //Collections.sort(sessions);
             return  sessions.get(childPosition);
        }
        catch (NullPointerException exception) {
            Log.d(LOG_TAG, "No daily sessions: " + exception);
            return null;
        }
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.schedule_expandable_parent, viewGroup, false);
        }
        ((TextView) view.findViewById(R.id.schedule_expandable_parent_name)).setText(DAYS[groupPosition]);
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.schedule_expandable_child, viewGroup, false);
        }
        try {
            final Session session = userWeeklyTimetable.getDailyTimetable(dates.get(groupPosition)).get(childPosition);

            ((TextView) view.findViewById(R.id.schedule_expandable_child_code)).setText(session.getModule_id());
            ((TextView) view.findViewById(R.id.schedule_expandable_child_name)).setText(userModules.get(session.getModule_id()));
            ((TextView) view.findViewById(R.id.schedule_expandable_child_type)).setText(session.getSessionType());
            String time = session.getTime_begin() + " - " + session.getTime_end();
            ((TextView) view.findViewById(R.id.schedule_expandable_child_time)).setText(time);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainFragmentDirections.ActionDetailedStats action = MainFragmentDirections.actionDetailedStats();
                    action.setModuleCode(session.getModule_id());
                    action.setModuleName(userModules.get(session.getModule_id()));
                    Navigation.findNavController(view).navigate(action);
                }
            });

        }
        catch (NullPointerException exception) {
            Log.d(LOG_TAG, "No session corresponding to child: " + exception);
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setUserWeeklyTimetable(TimeTable userWeeklyTimetable) {
        this.userWeeklyTimetable = userWeeklyTimetable;
        notifyDataSetChanged();
    }

    public void setUserModules(Map<String, String> userModules) {
        this.userModules = userModules;
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.d(LOG_TAG, "DataSet changed");
    }
}
