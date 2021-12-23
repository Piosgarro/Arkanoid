package com.gamp.android.arkanoid.sound;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.gamp.android.arkanoid.R;

public class Sound {

    private static SoundPool soundPool;
    private static int hitFlipper;
    private static int hitSound;
    private static int lostLife;
    private static int hitPowerUp;
    private static int scoreSound;
    private static int win;
    public static boolean soundBoolean;
    public static SharedPreferences mPrefs;

    /**
     * Costruttore del suono. Qui impostiamo la classe AudioAttributes e costruiamo l'oggetto
     * di tipo SoundPool con cui andremo a riprodurre i nostri suoni.
     *
     * @param  context  Il contesto relativo all'attività (this)
     */
    public Sound(Context context) {

        // Tramite SoundPool, riesco a riprodurre suoni come la vittoria,
        // partita persa, tocco sul mattone ecc.
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(5)
                .build();

        // Assegno il suono delle varie "action", alle variabili consone
        hitSound = soundPool.load(context, R.raw.hit_sound, 1);
        hitFlipper = soundPool.load(context, R.raw.hit_flipper, 1);
        hitPowerUp = soundPool.load(context, R.raw.power_up, 1);
        scoreSound = soundPool.load(context, R.raw.score_sound, 1);
        lostLife = soundPool.load(context, R.raw.lost_life, 1);
        win = soundPool.load(context, R.raw.win, 1);

        mPrefs = context.getSharedPreferences("save", 0);
        soundBoolean = mPrefs.getBoolean("valueSound", true);
    }

    /**
     * Vari metodi per poter riprodurre il suono.
     * Es. uso "playHitFlipper" quando la palla colpisce il Flipper, in modo tale che viene riprodotto il suono appropiato
     *
     */
    public void playHitSound() {
        if(soundBoolean)
            soundPool.play(hitSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playHitFlipper() {
        if(soundBoolean)
            soundPool.play(hitFlipper, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playHitPowerUp() {
        if(soundBoolean)
            soundPool.play(hitPowerUp, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playScoreSound() {
        if(soundBoolean)
            soundPool.play(scoreSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playLostLife() {
        if(soundBoolean)
            soundPool.play(lostLife, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playWin() {
        if(soundBoolean)
            soundPool.play(win, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
