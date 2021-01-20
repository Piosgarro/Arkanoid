package com.example.android.arkanoid;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

public class Sound {

    private static SoundPool soundPool;
    private static int hitFlipper;
    private static int hitSound;
    private static int lostLife;
    private static int hitPowerUp;
    private static int scoreSound;
    private static int win;

    public Sound(Context context) {

        // Tramite SoundPool, riesco a riprodurre suoni come la vittoria,
        // partita persa, tocco sul mattone ecc.
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(2)
                .build();

        // Assegno il suono delle varie "action", alle variabili consone
        hitSound = soundPool.load(context, R.raw.hit_sound, 1);
        hitFlipper = soundPool.load(context, R.raw.hit_flipper, 1);
        hitPowerUp = soundPool.load(context, R.raw.power_up, 1);
        scoreSound = soundPool.load(context, R.raw.score_sound, 1);
        lostLife = soundPool.load(context, R.raw.lost_life, 1);
        win = soundPool.load(context, R.raw.win, 1);
    }

    // Vari metodi per poter riprodurre il suono.
    // Es. uso "playHitFlipper" quando la palla colpisce il Flipper, in modo tale che viene riprodotto il suono appropiato
    public void playHitSound() {
        soundPool.play(hitSound,1.0f, 1.0f, 1,0,1.0f);
    }

    public void playHitFlipper() {
        soundPool.play(hitFlipper,1.0f, 1.0f, 1,0,1.0f);
    }

    public void playHitPowerUp() {
        soundPool.play(hitPowerUp,1.0f, 1.0f, 1,0,1.0f);
    }

    public void playScoreSound() {
        soundPool.play(scoreSound,1.0f, 1.0f, 1,0,1.0f);
    }

    public void playLostLife() {
        soundPool.play(lostLife,1.0f, 1.0f, 1,0,1.0f);
    }

    public void playWin() {
        soundPool.play(win,1.0f, 1.0f, 1,0,1.0f);
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}