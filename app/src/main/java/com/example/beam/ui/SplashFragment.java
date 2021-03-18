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
import androidx.navigation.fragment.NavHostFragment;

import com.example.beam.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SplashFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplashFragment extends Fragment {
    private View view;
    HashMap<View, Animator> animHashMap;

    public static SplashFragment newInstance(String param1, String param2) {

        SplashFragment fragment = new SplashFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.splash_fragment, container, false);

        animHashMap = new HashMap<>();
        addAnimToHashMap(R.id.beamTxt, R.animator.firstanim);
        addAnimToHashMap(R.id.blackBg, R.animator.bganim);
        addAnimToHashMap(R.id.whiteOverlay, R.animator.overlayanim);
        addAnimToHashMap(R.id.bars, R.animator.barsappear);
        addAnimToHashMap(R.id.bar_1, R.animator.bar_1);
        addAnimToHashMap(R.id.bar_2, R.animator.bar_2);
        addAnimToHashMap(R.id.bar_3, R.animator.bar_3);
        addAnimToHashMap(R.id.bar_4, R.animator.bar_4);
        addAnimToHashMap(R.id.bar_5, R.animator.bar_5);
        addAnimToHashMap(R.id.bar_6, R.animator.bar_6);

        for (Map.Entry<View, Animator> entry : animHashMap.entrySet()) {
            entry.getValue().setTarget(entry.getKey());
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animHashMap.values());
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    NavHostFragment.findNavController(SplashFragment.this).popBackStack();
                }
                else {
                    NavHostFragment.findNavController(SplashFragment.this).navigate(R.id.signin_fragment);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    private void addAnimToHashMap(int viewId, int animatorId) {
        animHashMap.put(view.findViewById(viewId), AnimatorInflater.loadAnimator((Activity) getActivity(), animatorId));
    }

}