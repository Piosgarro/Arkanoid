package com.example.android.arkanoid;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

public class MainActivity extends AppCompatActivity {

    private AnimationDrawable frameAnimation;
    private Game game;
    private Handler updateHandler;
    private UpdateThread myThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Imposta orientamento schermo
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /*
            Crea il gioco.
            --------------
            Parametri:
            - Vita
            - Score
         */
        game = new Game(this, 3, 0);

        setContentView(R.layout.activity_main);

        // Carica l'ImageView che ospiterà l'animazione e setta
        // lo sfondo attraverso la nostra risorsa XML gradient_anim
        ScrollView img = (ScrollView) findViewById(R.id.mainActivityLayout);
        img.setBackgroundResource(R.drawable.gradient_anim);

        // Carica lo sfondo, che è stato compilato come un oggetto AnimationDrawable
        frameAnimation = (AnimationDrawable) img.getBackground();

        // Starta l'animazione (Verrà loopata di default)
        frameAnimation.setEnterFadeDuration(1000);
        frameAnimation.setExitFadeDuration(3000);
        frameAnimation.start();

        // Crea un Gestore ed un Thread
        CreateHandler();
        myThread = new UpdateThread(updateHandler);
        myThread.start();
    }

    public void startGame(View v){
        // TODO - Creare attività invece di impostare un layout
        setContentView(game);

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
