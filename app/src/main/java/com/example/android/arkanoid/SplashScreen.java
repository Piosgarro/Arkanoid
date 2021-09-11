package com.example.android.arkanoid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static java.lang.Thread.sleep;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity implements View.OnTouchListener {

    private final Handler handler1 = new Handler(Looper.getMainLooper());
    private boolean isSplashRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Imposto il layout
        setContentView(R.layout.splash_screen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController controller = getWindow().getInsetsController();

            if (controller != null)
                controller.hide(WindowInsets.Type.statusBars());
        }
        else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        final TextView splashScreenText = findViewById(R.id.SplashScreenText);
        final TextView creditsOriginal = findViewById(R.id.creditsOriginal);
        final TextView divider = findViewById(R.id.divider);
        final TextView creditsRevamped = findViewById(R.id.creditsRevamped);
        final Animation slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.side_in_animation);
        splashScreenText.startAnimation(slideInAnimation);
        creditsOriginal.startAnimation(slideInAnimation);
        divider.startAnimation(slideInAnimation);
        creditsRevamped.startAnimation(slideInAnimation);

        handler1.postDelayed(() -> {
            try {
                sleep(0);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (isSplashRunning) {
                    Log.d("DEBUG", "Finishing splash activity from Thread");
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 3000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d("DEBUG", "Finishing splash activity because User touched the screen");
            isSplashRunning = false; //or in onPause
            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(i);
            finish();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        return false;
    }

    @Override
    protected void onPause() {
        isSplashRunning = false;
        super.onPause();
        finish();
    }

}
