package com.example.beam;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;


public class SplashFragment extends Fragment {
    private HashMap<View, Animator> animHashMap;
    private Context splashFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.splash_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        animHashMap = new HashMap<>();
        addAnimToHashMap(view, R.id.beamTxt, R.animator.firstanim);
        addAnimToHashMap(view, R.id.blackBg, R.animator.bganim);
        addAnimToHashMap(view, R.id.whiteOverlay, R.animator.overlayanim);
        addAnimToHashMap(view, R.id.bars, R.animator.barsappear);
        addAnimToHashMap(view, R.id.bar_1, R.animator.bar_1);
        addAnimToHashMap(view, R.id.bar_2, R.animator.bar_2);
        addAnimToHashMap(view, R.id.bar_3, R.animator.bar_3);
        addAnimToHashMap(view, R.id.bar_4, R.animator.bar_4);
        addAnimToHashMap(view, R.id.bar_5, R.animator.bar_5);
        addAnimToHashMap(view, R.id.bar_6, R.animator.bar_6);

        for (Map.Entry<View, Animator> entry : animHashMap.entrySet()) {
            entry.getValue().setTarget(entry.getKey());
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animHashMap.values());
        animatorSet.start();

    }

    private void addAnimToHashMap(View view, int viewId, int animatorId) {
        animHashMap.put(view.findViewById(viewId), AnimatorInflater.loadAnimator(splashFragment, animatorId));
    }
}