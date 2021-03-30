package com.example.beam.ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.beam.BeamViewModel;
import com.example.beam.R;
import com.example.beam.models.BeamUser;
import com.example.beam.models.TimeTable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SplashFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplashFragment extends Fragment {
    private View layoutView;
    private HashMap<View, Animator> anim1HashMap;
    private HashMap<View, Animator> anim2HashMap;

    private BeamViewModel beamViewModel;
    private FirebaseAuth mAuth;

    public static SplashFragment newInstance(String param1, String param2) {

        SplashFragment fragment = new SplashFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beamViewModel = new ViewModelProvider(getActivity()).get(BeamViewModel.class);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.splash_fragment, container, false);

        anim1HashMap = new HashMap<>();
        anim2HashMap = new HashMap<>();
        addAnimToHashMap(anim1HashMap, R.id.beamTxt, R.animator.firstanim);
        addAnimToHashMap(anim2HashMap, R.id.blackBg, R.animator.bganim);
        addAnimToHashMap(anim2HashMap, R.id.whiteOverlay, R.animator.overlayanim);
        addAnimToHashMap(anim2HashMap, R.id.bars, R.animator.barsappear);
        addAnimToHashMap(anim2HashMap, R.id.bar_1, R.animator.bar_1);
        addAnimToHashMap(anim2HashMap, R.id.bar_2, R.animator.bar_2);
        addAnimToHashMap(anim2HashMap, R.id.bar_3, R.animator.bar_3);
        addAnimToHashMap(anim2HashMap, R.id.bar_4, R.animator.bar_4);
        addAnimToHashMap(anim2HashMap, R.id.bar_5, R.animator.bar_5);
        addAnimToHashMap(anim2HashMap, R.id.bar_6, R.animator.bar_6);

        for (Map.Entry<View, Animator> entry : anim1HashMap.entrySet()) {
            entry.getValue().setTarget(entry.getKey());
        }
        for (Map.Entry<View, Animator> entry : anim2HashMap.entrySet()) {
            entry.getValue().setTarget(entry.getKey());
        }
        return layoutView;
    }

    private void addAnimToHashMap(HashMap<View, Animator> hashMap, int viewId, int animatorId) {
        hashMap.put(layoutView.findViewById(viewId), AnimatorInflater.loadAnimator((Activity) getActivity(), animatorId));
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            AnimatorSet animatorSet = new AnimatorSet();
            List<Animator> animators = new ArrayList<>(anim1HashMap.values());
            animators.addAll(anim2HashMap.values());
            animatorSet.playTogether(animators);
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    NavHostFragment.findNavController(SplashFragment.this).navigate(R.id.signin_fragment);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animatorSet.start();
        }
        else {
            // Play blinking text animation on repeat indefinitely
            AnimatorSet animatorSet1 = new AnimatorSet();
            animatorSet1.playTogether(anim1HashMap.values());
            animatorSet1.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    animatorSet1.start();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animatorSet1.start();

            // Play bars animation once entire weekly timetable is loaded
            AnimatorSet animatorSet2 = new AnimatorSet();
            animatorSet2.playTogether(anim2HashMap.values());
            animatorSet2.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    NavHostFragment.findNavController(SplashFragment.this).popBackStack();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            beamViewModel.loadUser();
            beamViewModel.getUserDetails().observe(getViewLifecycleOwner(), new Observer<BeamUser>() {
                @Override
                public void onChanged(BeamUser beamUser) {
                    beamViewModel.initialLoad();
                    beamViewModel.getUserWeeklyTimetable().observe(getViewLifecycleOwner(), new Observer<TimeTable>() {
                        @Override
                        public void onChanged(TimeTable timeTable) {
                            if (timeTable.getWeeklyTimetable().size() == 7) {
                                animatorSet1.cancel();
                                animatorSet2.start();
                            }
                        }
                    });
                }
            });
        }
    }
}