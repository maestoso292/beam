package com.example.beam.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.example.beam.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ScheduleExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private LinkedHashMap<String, List<ScheduleDataPump.TestModule>> expandableListData;
    private List<String> expandableGroupNames;

    ScheduleExpandableListAdapter(Context context) {
        expandableListData = ScheduleDataPump.getData();
        expandableGroupNames = new ArrayList<>(expandableListData.keySet());
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return expandableListData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return expandableListData.get(expandableGroupNames.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return expandableGroupNames.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return expandableListData.get(expandableGroupNames.get(groupPosition)).get(childPosition);
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
        ((TextView) view.findViewById(R.id.schedule_expandable_parent_name)).setText(expandableGroupNames.get(i));
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ScheduleDataPump.TestModule session = expandableListData.get(expandableGroupNames.get(i)).get(i1);
        if (view == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.schedule_expandable_child, viewGroup, false);
        }
        ((TextView) view.findViewById(R.id.schedule_expandable_child_name)).setText(session.name);
        ((TextView) view.findViewById(R.id.schedule_expandable_child_time)).setText(session.time);
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
}
