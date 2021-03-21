package com.example.beam.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainFragmentAdapter extends FragmentStateAdapter {
    private List<String> tabHeadings;
    private List<Fragment> fragmentList;

    MainFragmentAdapter(Fragment fragment) {
        super(fragment);

        tabHeadings = new ArrayList<>(4);
        tabHeadings.add("Home");
        tabHeadings.add("Daily");
        tabHeadings.add("Weekly");
        tabHeadings.add("Stats");

        fragmentList = new ArrayList<>(4);
        fragmentList.add(0, new HomeFragment());
        fragmentList.add(1, new TodayFragment());
        fragmentList.add(2, new ScheduleFragment());
        fragmentList.add(3, new StatsFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    public List<String> getTabHeadings() {
        return tabHeadings;
    }
}
