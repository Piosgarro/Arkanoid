package com.example.android.arkanoid;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private Handler handler1 = new Handler(Looper.getMainLooper());

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
            //noinspection deprecation
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

        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 3000);
    }
}
