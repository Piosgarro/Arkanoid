package com.gamp.android.arkanoid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity implements View.OnTouchListener {

    // Creiamo un oggetto di tipo Handler & Runnable
    private final Handler h = new Handler();
    private Runnable r = null;

    /**
     * onCreate per la Splash Screen
     * Carichiamo l'XML della Splash Screen, impostiamo lo schermo a FullScreen e avviamo l'animazione
     *
     * @param savedInstanceState default di Android
     */
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

        // Impostiamo un oggetto Runnable, il quale ci chiude la SplashScreen e ci avvia la MainActivity
        r = () -> {
            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(i);
            finish();
        };

        // Attraverso l'Handler creato a inizio classe, aspettiamo 3000ms (3 secondi), dopo di che
        // avvia il metodo run() dell'oggetto Runnable creato precedentemente. Sostanzialmente quello che
        // si cerca di fare con questo è:
        // - Aspetto 3 secondi, se l'utente non ha cliccato lo schermo (facendo chiudere l'Activity), allora la chiudo
        //   a prescindere, per evitare che lo Splash Screen rimanga sempre attivo senza mai chiudersi.
        h.postDelayed(r, 3000);
    }

    /**
     * Metodo relativo al tocco su schermo
     * Se tocchiamo lo schermo, finiamo l'attività e avviamo la MainActivity.
     * @param event default di Android
     * @return Ritorna Vero o Falso
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d("DEBUG", "Finishing splash activity because User touched the screen");
            // Il removeCallbacks(r) ci serve per indicare che quando clicchiamo lo schermo
            // (e che quindi chiudiamo la SplashScreen), non c'è più bisogno di invocare il metodo
            // run() relativo al Runnable (Riga 63).
            // Senza questo, accadeva che nel caso in cui toccavamo lo schermo prima che si chiudesse
            // automaticamente, veniva comunque invocato il metodo run() e quindi la MainActivity
            // si chiudeva e riapriva nuovamente
            h.removeCallbacks(r);
            Intent i = new Intent(this, MainActivity.class);
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
        super.onPause();
        finish();
    }

}
