package com.example.android.arkanoid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

public class Brick extends View {

    private Bitmap brick; // Contenitore per l'immagine del mattone
    private float x; // Posizione del mattone sull'asse orizzontale
    private float y; // Posizione del mattone sull'asse verticale
    private int lifes; // Vita del Brick

    public Brick(Context context, float x, float y, int lifes) {
        super(context);
        this.x = x;
        this.y = y;
        this.lifes = lifes;
        skin();
    }

    // Assegna un colore random al mattone (brick)
    private void skin() {
        // Random da 0 a 8
        int a = (int) (Math.random() * 8);
        switch (a) {
            case 0:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_aqua);
                break;
            case 1:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_blue);
                break;
            case 2:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_green);
                break;
            case 3:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_orange);
                break;
            case 4:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_pink);
                break;
            case 5:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_purple);
                break;
            case 6:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_red);
                break;
            case 7:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_yellow);
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

    public int getLifes() {
        return lifes;
    }

    public void setLifes(int lifes) {
        this.lifes = lifes;
    }

    public Bitmap getBrick() {
        return brick;
    }
}
