package com.example.beam;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {
    ImageView blackbg;
    ImageView overlay;
    ImageView bar1;
    ImageView bar2;
    ImageView bar3;
    ImageView bar4;
    ImageView bar5;
    ImageView bar6;
    TextView beamtxt;
    ConstraintLayout bars;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beamtxt = (TextView) findViewById(R.id.beamTxt);
        blackbg = (ImageView) findViewById(R.id.blackBg);
        overlay = (ImageView) findViewById(R.id.whiteOverlay);
        bar1 = (ImageView) findViewById(R.id.bar_1);
        bar2 = (ImageView) findViewById(R.id.bar_2);
        bar3 = (ImageView) findViewById(R.id.bar_3);
        bar4 = (ImageView) findViewById(R.id.bar_4);
        bar5 = (ImageView) findViewById(R.id.bar_5);
        bar6 = (ImageView) findViewById(R.id.bar_6);
        bars = (ConstraintLayout) findViewById (R.id.bars);


        AnimatorSet blinktxt = (AnimatorSet) AnimatorInflater.loadAnimator(beamtxt.getContext(),
                R.animator.firstanim);
        blinktxt.setTarget(beamtxt);
        blinktxt.start();

        AnimatorSet bgAnim = (AnimatorSet) AnimatorInflater.loadAnimator(blackbg.getContext(),
                R.animator.bganim);
        bgAnim.setTarget(blackbg);
        bgAnim.start();

/*//      View animation tryout (might not need it anymore)
        Animation shrinkdown = AnimationUtils.loadAnimation(this, R.anim.shrinkdown);
        blackbg.startAnimation(shrinkdown);*/

        AnimatorSet overlayAnim = (AnimatorSet) AnimatorInflater.loadAnimator(overlay.getContext(),
                R.animator.overlayanim);
        overlayAnim.setTarget(overlay);
        overlayAnim.start();

        AnimatorSet barsAppear = (AnimatorSet) AnimatorInflater.loadAnimator(bars.getContext(),
                R.animator.barsappear);
        barsAppear.setTarget(bars);
        barsAppear.start();

        AnimatorSet bar1scale = (AnimatorSet) AnimatorInflater.loadAnimator(bar1.getContext(),
                R.animator.bar_1);
        bar1scale.setTarget(bar1);
        bar1scale.start();

        AnimatorSet bar2scale = (AnimatorSet) AnimatorInflater.loadAnimator(bar2.getContext(),
                R.animator.bar_2);
        bar2scale.setTarget(bar2);
        bar2scale.start();

        AnimatorSet bar3scale = (AnimatorSet) AnimatorInflater.loadAnimator(bar3.getContext(),
                R.animator.bar_3);
        bar3scale.setTarget(bar3);
        bar3scale.start();

        AnimatorSet bar4scale = (AnimatorSet) AnimatorInflater.loadAnimator(bar4.getContext(),
                R.animator.bar_4);
        bar4scale.setTarget(bar4);
        bar4scale.start();

        AnimatorSet bar5scale = (AnimatorSet) AnimatorInflater.loadAnimator(bar5.getContext(),
                R.animator.bar_5);
        bar5scale.setTarget(bar5);
        bar5scale.start();

        AnimatorSet bar6scale = (AnimatorSet) AnimatorInflater.loadAnimator(bar6.getContext(),
                R.animator.bar_6);
        bar6scale.setTarget(bar6);
        bar6scale.start();




    }


}