package com.example.android.arkanoid;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

public class StartGame extends AppCompatActivity {

    private Game game;
    public static Sound sound;

    public static Activity activity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Imposta orientamento schermo
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sound = new Sound(this);
        game = new Game(this, 3, 0);
        setContentView(game);

        activity = this;

    }

    // Metti in pausa il gioco
    protected void onPause() {
        super.onPause();
        game.pauseGame();
    }

    // Riprendi il gioco
    protected void onResume() {
        super.onResume();
        game.resumeGame();
    }

    // Se premo indietro, mentre sto giocando, significa che l'utente vuole chiudere
    // la partita in corso, quindi stoppo il Thread relativo al gioco
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
