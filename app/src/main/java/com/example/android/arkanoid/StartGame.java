package com.example.android.arkanoid;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

public class StartGame extends AppCompatActivity {

    private Game game;
    private Handler updateHandler;
    private UpdateThread myThread;
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

        // Crea un Gestore ed un Thread
        CreateHandler();
        myThread = new UpdateThread(updateHandler);
        myThread.start();

        activity = this;

    }

    private void CreateHandler() {
        updateHandler = new Handler() {
            public void handleMessage(Message msg) {
                game.invalidate();
                game.update();
                super.handleMessage(msg);
            }
        };
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
}
