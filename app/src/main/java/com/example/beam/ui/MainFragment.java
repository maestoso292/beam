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
import com.example.beam.models.BeamUser;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class MainFragment extends Fragment {
    private ViewPager2 pager;
    private TabLayout tabLayout;
    private MainFragmentAdapter adapter;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private NavController navController;
    private BeamViewModel beamViewModel;

    private BeamUser userDetails;
    private Map<String, String> userModules;

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

            // Manual timetable insertion
            /*
            List<Session> sessions = new ArrayList<>();
            Map<String, List<Session>> map = new HashMap<>();
            sessions.add(new Session("COMP1000", "Lecture", "1000", "1200"));
            map.put("COMP1000", sessions);
            Calendar calendar = Calendar.getInstance();
            String date = String.format("%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)+3);
            for (Map.Entry<String, List<Session>> entry : map.entrySet()) {
                for(Session session : entry.getValue()) {
                    mDatabase.child("timetableTest").child(date).child(entry.getKey()).push().setValue(session);
                }
            }

             */
        }
    }
}
