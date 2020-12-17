package com.example.android.arkanoid;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ScrollView;

import static android.view.Gravity.START;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Imposta orientamento schermo
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Setta l'animazione prima di impostare il layout
        setAnimation();

        setContentView(R.layout.activity_main);

        // Carica l'ImageView che ospiterà l'animazione e setta
        // lo sfondo attraverso la nostra risorsa XML gradient_anim
        ScrollView img = (ScrollView) findViewById(R.id.mainActivityLayout);
        img.setBackgroundResource(R.drawable.gradient_anim);

        // Carica lo sfondo, che è stato compilato come un oggetto AnimationDrawable
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

        // Starta l'animazione (Verrà loopata di default)
        frameAnimation.setEnterFadeDuration(1000);
        frameAnimation.setExitFadeDuration(3000);
        frameAnimation.start();

    }

    // Imposta la transizione
    public void setAnimation()
    {
        Slide slide = new Slide();
        slide.setSlideEdge(START);
        slide.setDuration(200);
        slide.setInterpolator(new AccelerateDecelerateInterpolator());
        getWindow().setExitTransition(slide);
        getWindow().setEnterTransition(slide);

    }

    public void startGame(View v){
        //Instanzia una nuova attività
        Intent i = new Intent(MainActivity.this, StartGame.class);

        // Avvia la nuova attività attraverso un fade
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
        startActivity(i, options.toBundle());

    }

    public void startSettings(View v) {
        //Instanzia una nuova attività
        Intent i = new Intent(MainActivity.this, StartSettings.class);

        // Avvia la nuova attività attraverso un fade
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
        startActivity(i, options.toBundle());

    }
}
