package com.example.android.arkanoid;

import android.content.Context;
import android.media.MediaPlayer;

public final class Sound {

    public static MediaPlayer hitFlipper, hitSound, lostLife, hitPowerUp, scoreSound, winSound;

    public Sound(Context context) {
        hitFlipper = MediaPlayer.create(context, R.raw.hit_flipper);
        hitSound = MediaPlayer.create(context, R.raw.hit_sound);
        lostLife = MediaPlayer.create(context, R.raw.lost_life);
        hitPowerUp = MediaPlayer.create(context, R.raw.power_up);
        scoreSound = MediaPlayer.create(context, R.raw.score_sound);
        winSound = MediaPlayer.create(context, R.raw.win);
    }

}
