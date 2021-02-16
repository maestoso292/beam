package com.example.beam;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    HashMap<View, Animator> animHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

    private void addAnimToHashMap(int viewId, int animatorId) {
        animHashMap.put(findViewById(viewId), AnimatorInflater.loadAnimator(this, animatorId));
    }
}