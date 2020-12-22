package com.example.android.arkanoid;

public class Flipper {

    private float x; // Posizione del Flipper sull'asse orizzontale
    private final float y; // Posizione del Flipper sull'asse verticale

    public Flipper(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

}
