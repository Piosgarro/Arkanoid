package com.example.android.arkanoid;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Timer;
import java.util.TimerTask;

import static android.media.MediaPlayer.create;

public class MainActivity extends AppCompatActivity {

    // MediaPlayer
    public static MediaPlayer mediaPlayer;
    private float volume = 1;  // Imposta il volume del MediaPlayer
    public int lenght; // Punto preciso in cui si fermerà l'audio
    private int lenghtWhenPressBackButton;
    private int lenghtWhenPressHomeButton;
    private int lenghtWhenScreenOff;

    private boolean gameStarted;
    private boolean settingStarted;
    private boolean screenOff;
    private boolean userClickedHomeButton;
    private boolean userClickedBackButton;
    private static boolean musicSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Imposto il layout
        setContentView(R.layout.activity_main);

        // Controllo lo stato della Switch tramite getSharedPreferences
        // @param save = Il nome della SharedPreferences
        // @param valueMusic = L'ID del Boolean della switch
        SharedPreferences sharedpreferences = getSharedPreferences("save", Context.MODE_PRIVATE);
        musicSwitch = sharedpreferences.getBoolean("valueMusic", true);

        // Creo un mediaPlayer per poter inizializzare la musica
        mediaPlayer = new MediaPlayer();
        mediaPlayer = create(this, R.raw.main_soundtrack);
        mediaPlayer.setLooping(true);

        // Se la switch della Musica è attiva, starta la musica
        if (musicSwitch) {
            mediaPlayer.start();
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

    }


    public void startFadeOut() {

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

        timer.schedule(timerTask, FADE_INTERVAL, FADE_INTERVAL);
    }

    private void fadeOutStep(float deltaVolume) {
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

        timer.schedule(timerTask, FADE_INTERVAL, FADE_INTERVAL);
    }

    // Libera il Player dalla memoria
    private void stopPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    // Cattura il pulsante Back
    // Una volta che l'utente preme il tasto "Indietro", possiamo
    // caricare un nostro codice personale
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Controllo lo switch della musica
        SharedPreferences sharedpreferences = getSharedPreferences("save", Context.MODE_PRIVATE);
        musicSwitch = sharedpreferences.getBoolean("valueMusic", true);

        // Se lo switch è attivo e non ho avviato la schermata delle Impostazioni
        // significa che l'utente è uscito dall'App
        if (musicSwitch && !settingStarted) {
            userClickedBackButton = true;
            mediaPlayer.pause(); // Pausa la musica
            lenghtWhenPressBackButton = mediaPlayer.getCurrentPosition(); // Conserva in che punto ho stoppato la musica
        }
    }

    // Cattura il pulsante Home
    // Una volta che l'utente preme il tasto "Home", possiamo
    // caricare un nostro codice personale
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        // Controllo lo switch della musica
        SharedPreferences sharedpreferences = getSharedPreferences("save", Context.MODE_PRIVATE);
        musicSwitch = sharedpreferences.getBoolean("valueMusic", true);

        // Se lo switch è attivo e non ho avviato la schermata delle Impostazioni
        // significa che l'utente è uscito dall'App
        if (musicSwitch && !settingStarted) {
            userClickedHomeButton = true;
            mediaPlayer.pause(); // Pausa la musica
            lenghtWhenPressHomeButton = mediaPlayer.getCurrentPosition(); // Conserva in che punto ho stoppato la musica
        }
    }

    // onResume permette di caricare un nostro codice personale quando
    // l'attività "MainActivity" viene ripresa.
    @Override
    public void onResume() {
        super.onResume();

        // Controllo lo switch della musica
        SharedPreferences sharedpreferences = getSharedPreferences("save", Context.MODE_PRIVATE);
        musicSwitch = sharedpreferences.getBoolean("valueMusic", true);

        // Se ho precedentemente visualizzato la pagina delle Impostazioni, la resetto
        if (settingStarted) {
            settingStarted = false;
        }

        // Se lo switch della musica è attivo, faccio i vari controlli
        // per poter mandare in FadeIn (transizione) la musica se l'utente
        // torna in MainActivity, dopo aver cliccato il pulsante Indietro, Home oppure
        // dopo aver iniziato una partita
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
            if (screenOff) {
                screenOff = false;
                mediaPlayer.seekTo(lenghtWhenScreenOff);
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

        // If the screen is off then the device has been locked
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean isScreenOn;
        isScreenOn = powerManager.isInteractive();

        if (!isScreenOn) {
            screenOff = true;
            mediaPlayer.pause(); // Pausa la musica
            lenghtWhenScreenOff = mediaPlayer.getCurrentPosition(); // Conserva in che punto ho stoppato la musica
        }
    }

}
