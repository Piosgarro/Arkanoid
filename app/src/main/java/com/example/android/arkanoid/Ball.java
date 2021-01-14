package com.example.android.arkanoid;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Ball {

    protected float xSpeed; // Velocità orizzontale
    protected float ySpeed; // Velocità verticale
    private float x; // Posizione della palla sull'asse orizzontale
    private float y; // Posizione della palla sull'asse verticale

    private float dx,dy;
    private float cx;
    private float cy;
    private float radius;
    private Paint pen;

    public Ball(float cx, float cy, float radius)
    {
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        this.dx = 0;
        this.dy = 0;
        this.pen = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setCx(float cx) {
        this.cx = cx;
    }

    public void setCy(float cy) {
        this.cy = cy;
    }

    public void setDx(float dx) {
        this.dx = dx;
    }

    public void setDy(float dy) {
        this.dy = dy;
    }

    public float getCx() {
        return cx;
    }

    public float getCy() {
        return cy;
    }

    public float getRadius() {
        return radius;
    }

    public void draw(Canvas canvas)
    {
        pen.setColor(Color.WHITE);
        canvas.drawCircle(cx,cy,radius,pen);
    }

    public boolean move(int w, int h)
    {
        this.cx+= dx;
        this.cy+= dy;

        // check if ball out of left or right side
        if((cx-radius) <= 0 || (cx+radius) >= w) {
            dx = -dx;
            StartGame.sound.playHitSound();
            return true;
        }

        // check if ball out of up side
        if( (cy-radius) <= 150 ) {
            dy = -dy;
            StartGame.sound.playHitSound();
            return true;
        }

        if( (cy + radius) >= h ) {
            dx = -dx;
            StartGame.sound.playHitSound();
            return false;
        }
        return true;
    }

    public boolean collideWith(Brick brick)
    {

        float xBrick = brick.getX();
        float yBrick = brick.getY();

        float x, y;
        boolean bx = false;
        boolean by = false;

        if (cx < xBrick) {
            x = xBrick;
            bx = true;
        } else if (cx > (xBrick + 100)) {
            x = xBrick + 100;
            bx = true;
        } else {
            x = cx;
        }
        if (cy < yBrick) {
            y = yBrick;
            by = true;
        } else if (cy > (yBrick + 80)) {
            y = yBrick + 80;
            by = true;
        } else {
            y = cy;
        }

        if (((x - cx) * (x - cx)) + ((y - cy) * (y - cy)) < radius*radius) {

            if (bx) {
                dx = -dx;
                StartGame.sound.playHitSound();
            }
            if (by) {
                dy = -dy;
                StartGame.sound.playHitSound();
            }
            return true;
        }
        return false;
    }

    public boolean collideWith(PowerUp powerup) {

        float xPowerUp = powerup.getX();
        float yPowerUp = powerup.getY();

        float x, y;

        if (cx < xPowerUp) {
            x = xPowerUp;
        } else if (cx > (xPowerUp + 100)) {
            x = xPowerUp + 100;
        } else {
            x = cx;
        }
        if (cy < yPowerUp) {
            y = yPowerUp;
        } else if (cy > (yPowerUp + 80)) {
            y = yPowerUp + 80;
        } else {
            y = cy;
        }

        if (((x - cx) * (x - cx)) + ((y - cy) * (y - cy)) < radius*radius) {
            StartGame.sound.playPowerUp();
            return true;
        }
        return false;
    }

    public void collideWith(Flipper flipper, int flipperWidth, int flipperHeight) {

        float x1,y1, x2,y2;
        x1 = flipper.getX();
        y1 = flipper.getY();

        x2 = flipper.getX() + flipperWidth;
        y2 = flipper.getY() + flipperHeight;

        // from up
        if( (cx >= x1 && cx <= x2) && (cy + radius) == y1) {
            dy = -dy;
            StartGame.sound.playHitSound();
            return;
        }

        //from right
        if( cx-radius == x2 && (cy >= y1 && cy <= y2)) {
            dx = -dx;
            StartGame.sound.playHitSound();
            return;
        }

        //from left
        if( cx+radius == x1 && (cy >= y1 && cy <= y2) ) {
            dx = -dx;
            StartGame.sound.playHitSound();
        }

    }

    // Crea una palla con velocità casuale
    protected void generateSpeed() {
        int maxX = 9;
        int minX = 7;
        int maxY = -7;
        int minY = -9;
        int rangeX = maxX - minX + 1;
        int rangeY = maxY - minY + 1;

        dx = ((int) (Math.random() * rangeX) + minX);
        dy = ((int) (Math.random() * rangeY) + minY);
        //dx = (float) 0.52;
        //dy = (float) -0.52;
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
        dx = dx + (level);
        dy = dy - (level);
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

}
