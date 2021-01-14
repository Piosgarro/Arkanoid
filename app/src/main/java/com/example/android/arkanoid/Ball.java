package com.example.android.arkanoid;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

public class Ball {

    private float dx,dy;
    private float cx;
    private float cy;
    private final float radius;
    private final Paint pen;

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

    public float getCx() {
        return cx;
    }

    public float getCy() {
        return cy;
    }

    public void setDx(float dx) {
        this.dx = dx;
    }

    public void setDy(float dy) {
        this.dy = dy;
    }

    public float getDx() {
        return dx;
    }

    public float getDy() {
        return dy;
    }

    public void draw(Canvas canvas, int color)
    {
        pen.setColor(color);
        canvas.drawCircle(cx,cy,radius,pen);
    }

    public boolean move(int w, int h, Flipper flipper, int flipperWidth) {
        this.cx+= dx;
        this.cy+= dy;

        // check if ball out of left or right side
        if((cx-radius) <= 0 || (cx+radius) >= w) {
            dx = -dx;
            StartGame.sound.playHitSound();
            return true;
        } else if((cy-radius) <= 145) { //up side
            dy = -dy;
            StartGame.sound.playHitSound();
            return true;
        } else if (cy+radius >= flipper.getY() && cy+radius <= (flipper.getY()+30) && cx-radius >= flipper.getX() && cx+radius <= (flipper.getX()+flipperWidth) && dy > 0) {
            dy = -dy;
            StartGame.sound.playHitSound();
        } else return !(cy + radius >= h);

        return true;
    }

    public boolean collideWith(Brick brick) {
        float xBrick = brick.getX();
        float yBrick = brick.getY();

        if (cx < xBrick) {
            xBrick -= cx;
        } else if (cx > (xBrick + 100)) {
            xBrick = cx - (xBrick + 100);
        } else {
            xBrick = 0;
        }
        if (cy < yBrick) {
            yBrick -= cy;
        } else if (cy > (yBrick + 80)) {
            yBrick = cy - (yBrick + 80);
        } else {
            yBrick = 0;
        }

        if ((xBrick * xBrick) + (yBrick * yBrick) <= radius * radius) {
            if ((yBrick == 0) || ((xBrick != 0) && (yBrick <= xBrick))) {
                dx = -dx;
            } else {
                dy = -dy;
            }
            StartGame.sound.playHitSound();
            return true;
        }
        return false;
    }

    public boolean collideWith(PowerUp powerup) {

        float x = powerup.getX() + 40 - cx;
        float y = powerup.getY() + 40 - cy;

        if (((x * x) + (y * y)) < (radius + 40) * (radius + 40)) {
            StartGame.sound.playPowerUp();
            return true;
        }
        return false;
    }

    // Crea una palla con velocità casuale
    protected void generateSpeed() {
        Random rand = new Random();
        int randomSign;
        if(rand.nextBoolean())
            randomSign = -1;
        else
            randomSign = 1;

        float maxX = 9;
        float minX = 7;
        float maxY = -7;
        float minY = -9;
        float rangeX = maxX - minX + 1;
        float rangeY = maxY - minY + 1;

        dx = ((float) ((Math.random() * rangeX) + minX)) * randomSign;
        dy = ((float) (Math.random() * rangeY) + minY);

        //dx = (float) 0.52;
        //dy = (float) -0.52;

    }

    // Aumenta velocità in base al livello
    protected void raiseSpeed(int level) {
        dx = dx + (level);
        dy = dy - (level);
    }
}
