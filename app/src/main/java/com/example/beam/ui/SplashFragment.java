package com.example.beam.ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
 * Fragment subclass for implementing Splash Screen. Plays BEAM animation. With no authentication,
 * navigate to sign in. Else, load user data and then back navigate to MainFragment.
 */
public class SplashFragment extends Fragment {
    private View layoutView;
    /** First half of animation. Blinking text. */
    private HashMap<View, Animator> anim1HashMap;
    /** Second half of animation. Reduction to bars and bar translation */
    private HashMap<View, Animator> anim2HashMap;

    private BeamViewModel beamViewModel;
    private FirebaseAuth mAuth;

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
        // Add blinking text to first half of animation
        addAnimToHashMap(anim1HashMap, R.id.beamTxt, R.animator.firstanim);
        // Add reduction to bars and bar translation to second half of animation
        addAnimToHashMap(anim2HashMap, R.id.blackBg, R.animator.bganim);
        addAnimToHashMap(anim2HashMap, R.id.whiteOverlay, R.animator.overlayanim);
        addAnimToHashMap(anim2HashMap, R.id.bars, R.animator.barsappear);
        addAnimToHashMap(anim2HashMap, R.id.bar_1, R.animator.bar_1);
        addAnimToHashMap(anim2HashMap, R.id.bar_2, R.animator.bar_2);
        addAnimToHashMap(anim2HashMap, R.id.bar_3, R.animator.bar_3);
        addAnimToHashMap(anim2HashMap, R.id.bar_4, R.animator.bar_4);
        addAnimToHashMap(anim2HashMap, R.id.bar_5, R.animator.bar_5);
        addAnimToHashMap(anim2HashMap, R.id.bar_6, R.animator.bar_6);

        // Set corresponding view as target for animation
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

    /**
     * If no authentication, play first half and second half of animations consecutively.
     * On animation end, navigate to SigninFragment.
     * If valid authentication, play first half of animation indefinitely.
     * Load user weekly timetable. Once full timetable loaded, cancel (end) first half of animation.
     * Play second half of animation.
     * On animation end, back navigate to MainFragment.
     */
    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // If no valid authentication, play animation then navigate to SigninFragment
        if (currentUser == null) {
            AnimatorSet animatorSet = new AnimatorSet();
            List<Animator> animators = new ArrayList<>(anim1HashMap.values());
            animators.addAll(anim2HashMap.values());
            animatorSet.playTogether(animators);
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {}

                @Override
                public void onAnimationEnd(Animator animator) {
                    NavHostFragment.findNavController(SplashFragment.this).navigate(R.id.signin_fragment);
                }

                @Override
                public void onAnimationCancel(Animator animator) {}

                @Override
                public void onAnimationRepeat(Animator animator) {}
            });
            animatorSet.start();
        }
        // Else, load data and then back navigate to MainFragment
        else {
            // Play blinking text animation on repeat indefinitely
            AnimatorSet animatorSet1 = new AnimatorSet();
            animatorSet1.playTogether(anim1HashMap.values());
            animatorSet1.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {}

                @Override
                public void onAnimationEnd(Animator animator) {
                    // Indefinitely play animation 1 unless cancelled.
                    animatorSet1.start();
                }

                @Override
                public void onAnimationCancel(Animator animator) {}

                @Override
                public void onAnimationRepeat(Animator animator) {}
            });
            animatorSet1.start();

            // Play bars animation once entire weekly timetable is loaded
            AnimatorSet animatorSet2 = new AnimatorSet();
            animatorSet2.playTogether(anim2HashMap.values());
            animatorSet2.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {}

                @Override
                public void onAnimationEnd(Animator animator) {
                    NavHostFragment.findNavController(SplashFragment.this).popBackStack();
                }

                @Override
                public void onAnimationCancel(Animator animator) {}

                @Override
                public void onAnimationRepeat(Animator animator) {}
            });
            // Load weekly timetable
            beamViewModel.loadUser();
            Log.d("Splash Fragment", "Loading User Details");
            beamViewModel.getUserDetails().observe(getViewLifecycleOwner(), new Observer<BeamUser>() {
                @Override
                public void onChanged(BeamUser beamUser) {
                    beamViewModel.initialLoad();
                    beamViewModel.getUserWeeklyTimetable().observe(getViewLifecycleOwner(), new Observer<TimeTable>() {
                        @Override
                        public void onChanged(TimeTable timeTable) {
                            // Wait until timetable for all 7 days of the week is loaded.
                            // Then cancel animation 1 and play animation 2
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