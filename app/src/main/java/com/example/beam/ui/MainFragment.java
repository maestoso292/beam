package com.example.beam.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.beam.BeamViewModel;
import com.example.beam.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainFragment extends Fragment {
    private ViewPager2 pager;
    private TabLayout tabLayout;
    private MainFragmentAdapter adapter;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private NavController navController;
    private BeamViewModel beamViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        beamViewModel = new ViewModelProvider(getActivity()).get(BeamViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pager = view.findViewById(R.id.main_pager);
        //tabLayout = view.findViewById(R.id.main_tab_layout);
        adapter = new MainFragmentAdapter(this);

        pager.setAdapter(adapter);
        /*
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(adapter.getTabHeadings().get(position));
            }
        }
        );
        tabLayoutMediator.attach();

         */
    }

    @Override
    public void onResume() {
        super.onResume();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            navController.navigate(R.id.login_dest);
        }
        else {
            beamViewModel.initialLoad();
        }
    }

    public static class Module {
        public String sessionType;
        public String timeBegin;
        public String timeEnd;

        public Module(String sessionType, String timeStart, String timeEnd) {
            this.sessionType = sessionType;
            this.timeBegin = timeStart;
            this.timeEnd = timeEnd;
        }
    }

}
