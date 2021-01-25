package com.example.android.arkanoid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class PowerUp extends View {

    private Bitmap powerup; // Contenitore per l'immagine del powerUp
    private float x; // Posizione del powerUp sull'asse orizzontale
    private float y; // Posizione del powerUp sull'asse verticale

    public PowerUp(Context context, float x, float y) {
        super(context);
        this.x = x;
        this.y = y;
        skin();
    }

    // Necessario per rimuovere il warning sul costruttore
    public PowerUp(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    // Assegna una skin al powerup
    private void skin() {
        // Random da 0 a 2
        int a = (int) (Math.random() * 4);
        Log.d("D","PowerUp Number: " + a);
        switch (a) {
            case 0:
                powerup = BitmapFactory.decodeResource(getResources(), R.drawable.power_up_0);
                break;
            case 1:
                powerup = BitmapFactory.decodeResource(getResources(), R.drawable.power_up_1);
                break;
            case 2:
                powerup = BitmapFactory.decodeResource(getResources(), R.drawable.power_up_2);
                break;
            case 3:
                powerup = BitmapFactory.decodeResource(getResources(), R.drawable.power_up_3);
                break;
        }
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
