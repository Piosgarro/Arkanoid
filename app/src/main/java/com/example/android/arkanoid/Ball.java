package com.example.android.arkanoid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

public class Ball {

    private float dx,dy;
    private float cx, cy;
    private final float radius;
    private final Paint pen;

    private final float A = (float) Math.toRadians(20);
    private final float minA = (float) Math.toRadians(15);
    private final float maxA = (float) Math.toRadians(85);

    public Ball(float cx, float cy, float radius, Context context)
    {
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        this.dx = 0;
        this.dy = 0;
        this.pen = new Paint(Paint.ANTI_ALIAS_FLAG);
        new Sound(context);
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

    public boolean move(int w, int h, Flipper flipper, int flipperWidth, float vx) {
        this.cx+= dx;
        this.cy+= dy;

        // check if ball out of left or right side
        if ((cx-radius) <= 0 || (cx+radius) >= w) {
            dx = -dx;
            Sound.hitSound.start();
        }
        if ((cy-radius) <= 145) { //up side
            dy = -dy;
            Sound.hitSound.start();
        }
        if (cy+radius >= flipper.getY() && cy+radius <= (flipper.getY()+30) && cx-radius >= flipper.getX() && cx+radius <= (flipper.getX()+flipperWidth) && dy > 0) {

            // Rotate the ball movement vector according to flipper x velocity

            // new angle
            float angle = (float) Math.atan(dy/dx) - (vx/(1+Math.abs(vx)))*A;

            // bound the angle between minA e maxA
            if (dx > 0) {
                if (angle > maxA) angle = maxA;
                if (angle < minA) angle = minA;
            } else {
                if (angle < -maxA) angle = -maxA;
                if (angle > -minA) angle = -minA;
                angle += Math.PI;
            }

            // calculate and set the components of the new vector
            float sin = (float) Math.sin(angle);
            float cos = (float) Math.cos(angle);
            float speed = (float) Math.sqrt(dx*dx + dy*dy);

            dx = speed*cos;
            dy = -speed*sin;

            Sound.hitFlipper.start();
        } else {
            return !(cy - radius - 10 >= h);
        }

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
            Sound.hitSound.start();
            return true;
        }
        return false;
    }

    public boolean collideWith(PowerUp powerup) {

        float x = powerup.getX() + 40 - cx;
        float y = powerup.getY() + 40 - cy;

        if (((x * x) + (y * y)) < (radius + 40) * (radius + 40)) {
            Sound.hitPowerUp.start();
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

        //dx = ((float) ((Math.random() * rangeX) + minX)) * randomSign;
        //dy = ((float) (Math.random() * rangeY) + minY);

        dx = (float) 8;
        dy = (float) -8;

    }

    // Aumenta velocità in base al livello
    protected void raiseSpeed(int level) {
        dx = dx + (level);
        dy = dy - (level);
    }
}
