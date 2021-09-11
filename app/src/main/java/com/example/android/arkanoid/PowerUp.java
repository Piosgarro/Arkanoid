package com.example.android.arkanoid;

public class PowerUp {

    //                                      DEFAULT
    //                                  85, 5, 2, 5, 3
    public static final int[] weight = {85, 5, 2, 5, 3};

    private final float x; // Posizione del powerUp sull'asse orizzontale
    private float y; // Posizione del powerUp sull'asse verticale
    private int id; // Identificativo del powerup
    private final float fallingSpeed;

    public PowerUp(float x, float y, int id, float fallingSpeed) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.fallingSpeed = fallingSpeed;
    }

    public boolean move() {
        y += fallingSpeed;

        if (y >= Game.deviceHeight) {
            id = 0;
            return true;
            // Collisione rettangolo-rettangolo
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
