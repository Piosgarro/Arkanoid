package com.example.android.arkanoid;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

public class Sound {

    private static SoundPool soundPool;
    private static int hitFlipper;
    private static int hitSound;
    private static int scoreSound;

    public Sound(Context context) {

        //SoundPool deprecated in API 21
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(2)
                .build();

        hitSound = soundPool.load(context, R.raw.hit_sound, 1);
        hitFlipper = soundPool.load(context, R.raw.hit_flipper, 1);
        scoreSound = soundPool.load(context, R.raw.score_sound, 1);
    }

    public void playHitSound() {
        soundPool.play(hitSound,1.0f, 1.0f, 1,0,1.0f);
    }

    public void playHitFlipper() {
        soundPool.play(hitFlipper,1.0f, 1.0f, 1,0,1.0f);
    }

    public void playScoreSound() {
        soundPool.play(scoreSound,1.0f, 1.0f, 1,0,1.0f);
    }

}
