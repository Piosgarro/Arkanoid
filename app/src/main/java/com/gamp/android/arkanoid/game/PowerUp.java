package com.gamp.android.arkanoid.game;

import com.gamp.android.arkanoid.StartGame;

public class PowerUp {

    //                                      DEFAULT
    //                                  85, 5, 2, 5, 3
    public static final int[] weight = {85, 5, 2, 5, 3};

    private final float x; // Posizione del powerUp sull'asse orizzontale
    private float y; // Posizione del powerUp sull'asse verticale
    private int id; // Identificativo del powerup
    private final float fallingSpeed;

    /**
     * Costruttore dell'oggetto PowerUp
     *
     * @param  x  posizione del PowerUp sull'asse X
     * @param  y posizione del PowerUp sull'asse Y
     * @param id ID relativo al PowerUp
     * @param fallingSpeed velocità con cui cade il PowerUp
     */
    public PowerUp(float x, float y, int id, float fallingSpeed) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.fallingSpeed = fallingSpeed;
    }

    /**
     * Muovi il PowerUp sul canvas
     * Aggiungiamo la velocità (y) alla velocità attuale del PowerUp (fallingSpeed)
     * Controlliamo inoltre, se PowerUp sta scendendo oppure se ha beccato il Flipper
     */
    public boolean move() {
        y += fallingSpeed;

        if (y >= Game.deviceHeight) {
            id = 0;
            return true;
        // Collisione rettangolo-rettangolo (PowerUp - Flipper)
        } else if (y + Game.powerUpSide >= Game.flipper.top && y <= Game.flipper.bottom && x + Game.powerUpSide >= Game.flipper.left && x <= Game.flipper.right) {
            StartGame.sound.playHitPowerUp();
            return true;
        }
        return false;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getId() {
        return id;
    }

}
