package com.example.android.arkanoid;

public class Ball {

    protected float xSpeed;
    protected float ySpeed;
    private float x;
    private float y;

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
        xSpeed = xSpeed + (1 * level);
        ySpeed = ySpeed - (1 * level);
    }

    // Cambia direzione in base al muro che ha toccato
    // ed in base alla velocità
    // ---------------------------
    // Se si prova a cambiare le stringhe, la pallina esce dal range
    // del Frame
    protected void changeDirection(String string) {
        if (xSpeed > 0 && ySpeed < 0 && string.equals("prava")) {
            invertXSpeed();
        } else if (xSpeed > 0 && ySpeed < 0 && string.equals("hore")) {
            invertYSpeed();
        } else if (xSpeed < 0 && ySpeed < 0 && string.equals("hore")) {
            invertYSpeed();
        } else if (xSpeed < 0 && ySpeed < 0 && string.equals("lava")) {
            invertXSpeed();
        } else if (xSpeed < 0 && ySpeed > 0 && string.equals("lava")) {
            invertXSpeed();
        } else if (xSpeed > 0 && ySpeed > 0 && string.equals("dole")) {
            invertYSpeed();
        } else if (xSpeed > 0 && ySpeed > 0 && string.equals("prava")) {
            invertXSpeed();
        }
    }

    // Controlla se la palla è vicina al flipper
    private boolean isCloseToFlipper(float ax, float ay, float bx, float by) {
        bx += 12;
        by += 11;
        if ((Math.sqrt(Math.pow((ax + 50) - bx, 2) + Math.pow(ay - by, 2))) < 80) {
            return true;
        } else if ((Math.sqrt(Math.pow((ax + 100) - bx, 2) + Math.pow(ay - by, 2))) < 60) {
            return true;
        } else if ((Math.sqrt(Math.pow((ax + 150) - bx, 2) + Math.pow(ay - by, 2))) < 60) {
            return true;
        }
        return false;
    }

    // Controlla se la palla è vicina ad un mattone
    private boolean isCloseToBrick(float ax, float ay, float bx, float by) {
        bx += 12;
        by += 11;
        double d = Math.sqrt(Math.pow((ax + 50) - bx, 2) + Math.pow((ay + 40) - by, 2));
        return d < 80;
    }

    // Se la palla colpisce il Flipper, cambia direzione
    protected void hitFlipper(float xPaddle, float yPaddle) {
        if (isCloseToFlipper(xPaddle, yPaddle, getX(), getY())) {
            changeDirection();
        }
    }

    // Se la palla colpisce un mattone, cambia direzione
    protected boolean hitBrick(float xBrick, float yBrick) {
        if (isCloseToBrick(xBrick, yBrick, getX(), getY())) {
            changeDirection();
            return true;
        } else {
            return false;
        }
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

    public float getxSpeed() {
        return xSpeed;
    }

    public float getySpeed() {
        return ySpeed;
    }
}
