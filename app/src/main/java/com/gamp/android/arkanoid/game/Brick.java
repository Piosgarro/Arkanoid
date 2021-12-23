package com.gamp.android.arkanoid.game;

public class Brick {

    private final float x; // Posizione del mattone sull'asse orizzontale
    private final float y; // Posizione del mattone sull'asse verticale
    private int life; // Vita del Brick
    private final int color;

    /**
     * Costruttore dell'oggetto Brick
     *
     * @param  x  posizione del Brick sull'asse X
     * @param  y posizione del Brick sull'asse Y
     * @param life vite del Brick
     * @param color colore del Brick
     */
    public Brick(float x, float y, int life, int color) {
        this.x = x;
        this.y = y;
        this.life = life;
        this.color = color;
    }

    /**
     * Metodo per scalare le vite ai Brick
     *
     * @param  n  numero di vite da togliere al Brick
     * @return ritorna vero se le vite sono <= 0, falso altrimenti
     *         Ciò serve poichè se ritorniamo vero, allora togliamo il Brick dal gioco
     *         Vedere riferimento a Game.java (riga 426)
     */
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
