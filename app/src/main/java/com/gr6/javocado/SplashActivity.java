package com.gr6.javocado;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SplashActivity extends AppCompatActivity {

    // - ON CREATE METHOD: ENTRY POINT WHEN ACTIVITY IS CREATED - //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // - SET THE LAYOUT RESOURCE FOR THIS ACTIVITY - //
        setContentView(R.layout.activity_splash);

        // - FIND VIEWS FROM LAYOUT - //
        View rootView = findViewById(R.id.splashScreen);
        ImageView logoImage = findViewById(R.id.logoImage);
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);

        // - LOAD AND START SPLASH SCREEN ANIMATION ON LOGO AND TITLE - //
        Animation splashAnim = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        logoImage.startAnimation(splashAnim);
        toolbarTitle.startAnimation(splashAnim);

        // - SETUP BACKGROUND COLOR ANIMATION FROM TRANSPARENT TO GREEN DARK - //
        int colorFrom = ContextCompat.getColor(this, android.R.color.transparent);
        int colorTo = ContextCompat.getColor(this, R.color.greenDark);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(1000); // - DURATION 1 SECOND -
        colorAnimation.addUpdateListener(animator ->
                rootView.setBackgroundColor((int) animator.getAnimatedValue()));
        colorAnimation.start();

        // - DELAY 3.5 SECONDS THEN NAVIGATE TO MAIN ACTIVITY WITH FADE TRANSITION - //
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish(); // - FINISH SPLASH ACTIVITY SO IT IS REMOVED FROM BACK STACK - //
        }, 3500);
    }
}
