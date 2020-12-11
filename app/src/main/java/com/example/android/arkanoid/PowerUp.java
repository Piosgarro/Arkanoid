package com.example.android.arkanoid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

public class PowerUp extends View {

    private Bitmap powerup;
    private float x;
    private float y;

    public PowerUp(Context context, float x, float y) {
        super(context);
        this.x = x;
        this.y = y;
        skin();
    }

    // Assegna una skin al powerup
    private void skin() {
        powerup = BitmapFactory.decodeResource(getResources(), R.drawable.power_up);
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    public Bitmap getPowerUp() {
        return powerup;
    }

}
