package com.example.beam.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.beam.R;
import com.gauravbhola.ripplepulsebackground.RipplePulseLayout;

/**
 * Simple Fragment subclass inside Main Screen. Only for aesthetic purposes. Contains only a ripple
 * animation available by an open source library: https://github.com/gaurav414u/android-ripple-pulse-animation
 */
public class HomeFragment extends Fragment {
    /** XML of ripple animation element */
    RipplePulseLayout mRipplePulseLayout;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRipplePulseLayout = view.findViewById(R.id.layout_ripplepulse);
    }

    /**
     * On navigation to this screen, start the animation.
     */
    @Override
    public void onResume() {
        super.onResume();
        mRipplePulseLayout.startRippleAnimation();
    }
}