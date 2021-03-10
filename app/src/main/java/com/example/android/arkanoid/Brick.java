package com.example.android.arkanoid;

public class Brick {

    private final float x; // Posizione del mattone sull'asse orizzontale
    private final float y; // Posizione del mattone sull'asse verticale
    private int life; // Vita del Brick
    private final int color;

    public Brick(float x, float y, int life, int color) {
        this.x = x;
        this.y = y;
        this.life = life;
        this.color = color;
    }

    public boolean loseLife(int n) {
        life -= n;
        return life <= 0;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getLife() {
        return life;
    }

    public int getColor() {
        return color;
    }

}
