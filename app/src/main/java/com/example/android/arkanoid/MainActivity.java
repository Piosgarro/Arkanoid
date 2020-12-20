package com.example.android.arkanoid;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ScrollView;

import java.util.Timer;
import java.util.TimerTask;

import static android.media.MediaPlayer.create;
import static android.view.Gravity.START;

public class MainActivity extends AppCompatActivity {

    // MediaPlayer
    public static MediaPlayer mediaPlayer;
    private float volume = 1;  // Imposta il volume del MediaPlayer
    public int lenght; // Punto preciso in cui si fermerà l'audio
    private int lenghtWhenPressBackButton;
    private int lenghtWhenPressHomeButton;

    private boolean gameStarted;
    private boolean settingStarted;
    private boolean userClickedHomeButton;
    private boolean userClickedBackButton;
    private static boolean musicSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Imposta orientamento schermo
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Setta l'animazione prima di impostare il layout
        setAnimation();

        setContentView(R.layout.activity_main);

        // Controllo lo stato della Switch tramite getSharedPreferences
        // @param save = Il nome della SharedPreferences
        // @param valueMusic = L'ID del Boolean della switch
        SharedPreferences sharedpreferences = getSharedPreferences("save", Context.MODE_PRIVATE);
        musicSwitch = sharedpreferences.getBoolean("valueMusic", true);

        mediaPlayer = new MediaPlayer();
        mediaPlayer = create(this, R.raw.main_soundtrack);
        mediaPlayer.setLooping(true);

        if (musicSwitch) {
            mediaPlayer.start();
        }

        // Carica l'ImageView che ospiterà l'animazione e setta
        // lo sfondo attraverso la nostra risorsa XML gradient_anim
        ScrollView img = findViewById(R.id.mainActivityLayout);
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

        if (mediaPlayer.isPlaying()) {
            startFadeOut();
        }

        gameStarted = true;

        //Instanzia una nuova attività
        Intent i = new Intent(MainActivity.this, StartGame.class);

        // Avvia la nuova attività attraverso un fade
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
        startActivity(i, options.toBundle());

    }

    public void startSettings(View v) {

        settingStarted = true;

        //Instanzia una nuova attività
        Intent i = new Intent(MainActivity.this, StartSettings.class);

        // Avvia la nuova attività attraverso un fade
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
        startActivity(i, options.toBundle());

    }

    public void startFadeOut(){

        // Durata della transizione
        final int FADE_DURATION = 1000;

        // Tempo tra un cambio di volume e l'altro. Più piccolo è, più veloce sarà la transizione
        final int FADE_INTERVAL = 20;

        // Calcola il numero dei fades
        int numberOfSteps = FADE_DURATION / FADE_INTERVAL;

        // Calcola di quanto cambia il volume ad ogni step
        final float deltaVolume = volume / numberOfSteps;

        // Crea un Timer & Timer task per poter runnare la transizione
        final Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                //Crea un fadeOut
                fadeOutStep(deltaVolume);

                //Cancella ed elimina il timer quando è stato raggiunto il volume desiderato
                if (volume <= 0) {
                    timer.cancel();
                    timer.purge();
                    lenght = mediaPlayer.getCurrentPosition();
                    stopPlayer();
                }
            }
        };

        timer.schedule(timerTask,FADE_INTERVAL,FADE_INTERVAL);
    }

    private void fadeOutStep(float deltaVolume){
        mediaPlayer.setVolume(volume, volume);
        volume -= deltaVolume;
    }

    public void startFadeIn() {

        // Tempo tra un cambio di volume e l'altro. Più piccolo è, più veloce sarà la transizione
        final int FADE_INTERVAL = 200;

        // Valore della transizione - Il volume della musica aumenta di "fade" volte, ogni volta
        final double fade = 0.02083324;

        // Crea un Timer & Timer task per poter runnare la transizione
        final Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                // Cancella ed elimina il timer quando è stato raggiunto il volume desiderato
                if (volume >= 1) {
                    timer.cancel();
                    timer.purge();
                }

                mediaPlayer.setVolume(volume, volume);
                volume += fade;
            }
        };

        timer.schedule(timerTask,FADE_INTERVAL,FADE_INTERVAL);
    }

    // Libera il Player dalla memoria
    private void stopPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    // Cattura il pulsante Back
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences sharedpreferences = getSharedPreferences("save", Context.MODE_PRIVATE);
        musicSwitch = sharedpreferences.getBoolean("valueMusic", true);

        if (musicSwitch && !settingStarted) {
            userClickedBackButton = true;
            mediaPlayer.pause();
            lenghtWhenPressBackButton = mediaPlayer.getCurrentPosition();
        }
    }

    // Cattura il pulsante Home
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        SharedPreferences sharedpreferences = getSharedPreferences("save", Context.MODE_PRIVATE);
        musicSwitch = sharedpreferences.getBoolean("valueMusic", true);

        if (musicSwitch && !settingStarted) {
            userClickedHomeButton = true;
            mediaPlayer.pause();
            lenghtWhenPressHomeButton = mediaPlayer.getCurrentPosition();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedpreferences = getSharedPreferences("save", Context.MODE_PRIVATE);
        musicSwitch = sharedpreferences.getBoolean("valueMusic", true);

        if (settingStarted) {
            settingStarted = false;
        }

        if (musicSwitch) {
            if (userClickedHomeButton) {
                userClickedHomeButton = false;
                mediaPlayer.pause();
                mediaPlayer.seekTo(lenghtWhenPressHomeButton);
                startFadeIn();
                mediaPlayer.start();
            }
            if (userClickedBackButton) {
                userClickedBackButton = false;
                mediaPlayer.pause();
                mediaPlayer.seekTo(lenghtWhenPressBackButton);
                startFadeIn();
                mediaPlayer.start();
            }
            if (gameStarted) {
                gameStarted = false;
                mediaPlayer.seekTo(lenght);
                startFadeIn();
                mediaPlayer.start();
            }
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                startFadeIn();
                mediaPlayer.start();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
