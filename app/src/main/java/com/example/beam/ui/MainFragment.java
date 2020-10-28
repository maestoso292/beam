package com.example.beam.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.beam.R;
import com.example.beam.SavedStateModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainFragment extends Fragment {
    private ViewPager2 pager;
    private TabLayout tabLayout;
    private MainFragmentAdapter adapter;

    private SavedStateModel savedStateModel;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navController = NavHostFragment.findNavController(this);
        savedStateModel = new ViewModelProvider(requireActivity()).get(SavedStateModel.class);
        /*
        savedStateModel.getAuthentication().observe(requireActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isAuthenticated) {
                if (!isAuthenticated) {
                    navController.navigate(R.id.login_dest);
                    Log.d("Navigation", "Navigated to login");
                }
            }
        });
         */
        /*
        NavBackStackEntry navBackStackEntry = navController.getCurrentBackStackEntry();
        SavedStateHandle savedStateHandle = navBackStackEntry.getSavedStateHandle();
        MutableLiveData<Boolean> temp = savedStateHandle.getLiveData(LoginFragment.LOGIN_SUCCESSFUL, false);
        temp.observe(requireActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isAuthenticated) {
                if (!isAuthenticated) {
                    navController.navigate(R.id.login_dest);
                    Log.d("Navigation", "Failed to authenticate. Navigated to login");
                }
            }
        });
         */
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO SavedStateModel should store User class (?) in final product
        /*
        if (!savedStateModel.getAuthentication().getValue()) {
            navController.navigate(R.id.login_dest);
        }

         */
        savedStateModel.getAuthentication().observe(requireActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isAuthenticated) {
                if (!isAuthenticated) {
                    navController.navigate(R.id.login_dest);
                    Log.d("Navigation", "Navigated to login");
                }
            }
        });

        pager = view.findViewById(R.id.main_pager);
        tabLayout = view.findViewById(R.id.main_tab_layout);
        adapter = new MainFragmentAdapter(this);

        pager.setAdapter(adapter);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(adapter.getTabHeadings().get(position));
            }
        }
        );
        tabLayoutMediator.attach();
    }
}
