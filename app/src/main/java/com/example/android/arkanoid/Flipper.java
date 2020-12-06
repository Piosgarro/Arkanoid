package com.example.android.arkanoid;

public class Flipper {

    private float x;
    private float y;

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
