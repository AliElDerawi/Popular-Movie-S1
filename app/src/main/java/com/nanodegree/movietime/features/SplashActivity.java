package com.nanodegree.movietime.features;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import com.nanodegree.movietime.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    private static final int DELAY_TO_OPEN_MAIN_ACTIVITY_SCREEN = 2000;
    private Handler mHandler;
    private Runnable mRunnable;
    @BindView(R.id.progressBar)ProgressBar mProgressBar ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        ObjectAnimator anim = ObjectAnimator.ofInt(mProgressBar, "progress", 0, 100);
        anim.setDuration(15000);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();

    }


    @Override
    protected void onStart() {
        super.onStart();

        mRunnable = new Runnable() {
            @Override
            public void run() {

                Intent fromSplashActivity = new Intent(getApplicationContext(),HomeActivity.class);
                fromSplashActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(fromSplashActivity);
                finish();
            }
        };

        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, DELAY_TO_OPEN_MAIN_ACTIVITY_SCREEN);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }
}
