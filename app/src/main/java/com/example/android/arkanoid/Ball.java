package com.example.android.arkanoid;

public class Ball {

    protected float xSpeed; // Velocità orizzontale
    protected float ySpeed; // Velocità verticale
    private float x; // Posizione della palla sull'asse orizzontale
    private float y; // Posizione della palla sull'asse verticale

    public Ball(float x, float y) {
        this.x = x;
        this.y = y;
        generateSpeed();
    }

    // Crea una palla con velocità casuale
    protected void generateSpeed() {
        int maxX = 13;
        int minX = 7;
        int maxY = -17;
        int minY = -23;
        int rangeX = maxX - minX + 1;
        int rangeY = maxY - minY + 1;

        xSpeed = (int) (Math.random() * rangeX) + minX;
        ySpeed = (int) (Math.random() * rangeY) + minY;
    }

    // Cambia direzione in base alla velocità
    protected void changeDirection() {
        if (xSpeed > 0 && ySpeed < 0) {
            invertXSpeed();
        } else if (xSpeed < 0 && ySpeed < 0) {
            invertYSpeed();
        } else if (xSpeed < 0 && ySpeed > 0) {
            invertXSpeed();
        } else if (xSpeed > 0 && ySpeed > 0) {
            invertYSpeed();
        }
    }

    // Aumenta velocità in base al livello
    protected void raiseSpeed(int level) {
        xSpeed = xSpeed + (level);
        ySpeed = ySpeed - (level);
    }

    // Controlla se la palla è vicina ad un mattone
    private boolean isCloseToBrick(float ax, float ay, float bx, float by) {
        bx += 12;
        by += 11;
        double d = Math.sqrt(Math.pow((ax + 50) - bx, 2) + Math.pow((ay + 40) - by, 2));
        return d < 80;
    }


    // Se la palla colpisce un mattone, cambia direzione
    protected boolean hitBrick(float xBrick, float yBrick) {
        if (isCloseToBrick(xBrick, yBrick, getX(), getY())) {
            StartGame.sound.playHitSound(); // Se colpisci il mattone, attiva il suono relativo
            changeDirection();
            return true;
        } else {
            return false;
        }
    }

    // Se la palla colpisce un powerup, ritorna "true"
    // altrimenti falso
    protected boolean hitPowerUp(float xPowerUp, float yPowerUp) {
        return isCloseToPowerUp(xPowerUp, yPowerUp, getX(), getY());
    }

    // Controlla se la palla è vicino al powerup
    private boolean isCloseToPowerUp(float ax, float ay, float px, float py) {
        px += 12;
        py += 11;
        double d = Math.sqrt(Math.pow((ax + 50) - px, 2) + Math.pow((ay + 40) - py, 2));
        if (d < 80) {
            StartGame.sound.playPowerUp(); // Se colpisci il PowerUp, attiva il suono relativo
                                           // Non c'è il cambio della direzione, perchè non vogliamo
                                           // che la palla cambi direzione quando tocca il PowerUp
            return true;
        }
        return false;
    }

    // Muovi la palla alla velocità indicata
    protected void move() {
        x = x + xSpeed;
        y = y + ySpeed;
    }

    public void invertXSpeed() {
        xSpeed = -xSpeed;
    }

    public void invertYSpeed() {
        ySpeed = -ySpeed;
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

    public void setY(float y) {
        this.y = y;
    }

}
