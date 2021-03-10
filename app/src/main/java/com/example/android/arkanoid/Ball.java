package com.example.android.arkanoid;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Ball {

    public float cx, cy;
    private final float radius;
    private float speed;
    private float dx, dy;
    private final Paint pen;
    private final Float[] pastPositions = new Float[50];
    private static final Float[] trailshape = new Float[25];

    private final float minA = (float) Math.toRadians(15);

    static {
        // calcola e salva (1 volta sola) 25 fattori decrescenti per i raggi della palle nella scia
        for (int i = 0; i < 25; i++) {
            float t = (float) (i + 1) / 26;
            trailshape[i] = (1 - t) * (1 - t);
        }
    }

    public Ball(float cx, float cy, float radius) {
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        speed = 1;
        pen = new Paint(Paint.ANTI_ALIAS_FLAG);

        // inizializza le posizioni passate con la corrente
        for (int i = 0; i < 50; i++) {
            if (i % 2 == 0) {
                pastPositions[i] = cx;
            } else {
                pastPositions[i] = cy;
            }
        }
    }

    // Imposta la velocità e o la direzione della palla
    // angle != 0 -> angle è il nuovo angolo della velocità
    // speed != 0 -> speed è il nuovo modulo della velocità
    // angle == 0 && speed == 0 non cambia nulla
    public void setVelocity(float speed, double angle) {

        if (speed == 0) {
            speed = this.speed;
        } else {
            speed /= this.speed;
            this.speed *= speed;
        }

        if (angle == 0) {
            dx *= speed;
            dy *= speed;
        } else {
            dx = (float) (speed * Math.cos(angle));
            dy = (float) (speed * Math.sin(angle));
        }
    }

    public void draw(Canvas canvas, int color) {

        // disegna la palla
        pen.setColor(color);
        pen.setAlpha(255);
        canvas.drawCircle(cx, cy, radius, pen);

        // disegna la scia (ultime 25 posizioni)
        pen.setAlpha(100);
        for (int i = 0; i < 50; i = i + 2) {
            canvas.drawCircle(pastPositions[i], pastPositions[i + 1], trailshape[i / 2] * radius, pen);
        }

        // ruota l'array di 2 celle a destra
        // sovrascrivendo i dati della 25^ posizione passata (pP[48] pP[49])
        for (int i = 47; i >= 0; i--) {
            pastPositions[i + 2] = pastPositions[i];
        }

        // inserisci la posizione attuale come prima posizione passata (pP[0] pP[1])
        pastPositions[0] = cx;
        pastPositions[1] = cy;

    }

    public boolean move() {

        int w = Game.deviceWidth;
        int h = Game.deviceHeight;

        cx += dx;
        cy += dy;

        // check if ball out of left or right side
        if ((cx - radius) <= 0 || (cx + radius) >= w) {
            dx = -dx;
            StartGame.sound.playHitSound();
        }
        if ((cy - radius) <= 0) { //up side
            dy = -dy;
            StartGame.sound.playHitSound();
        }
        if (cy + radius >= Game.flipper.top && cy + radius <= Game.flipper.top + 30 && cx >= Game.flipper.left - radius && cx <= Game.flipper.right + radius && dy > 0) {

            double angle = (((1 - ((cx - Game.flipper.left + radius) / (Game.flipper.width() + 2 * radius))) * Math.toRadians(150)) + Math.toRadians(15));
            setVelocity(0, angle);
            dy = -dy;
            StartGame.sound.playHitFlipper();
        }
        return !((cy - radius - 10) >= h);
    }

    public boolean collideWith(Brick brick) {
        float xBrick = brick.getX();
        float yBrick = brick.getY();

        if (cx < xBrick) {
            xBrick = cx - xBrick;
        } else if (cx > (xBrick + Game.brickWidth)) {
            xBrick = cx - (xBrick + Game.brickWidth);
        } else {
            xBrick = 0;
        }
        if (cy < yBrick) {
            yBrick = cy - yBrick;
        } else if (cy > (yBrick + Game.brickHeight)) {
            yBrick = cy - (yBrick + Game.brickHeight);
        } else {
            yBrick = 0;
        }

        if ((xBrick * xBrick) + (yBrick * yBrick) <= radius * radius) {
            if (yBrick == 0) {
                dx = -dx;
            } else if (xBrick == 0) {
                dy = -dy;
            } else {
                // Distanza (centro della palla - punto sul rettangolo più vicino al centro della palla)
                float norma = (float) Math.sqrt((xBrick * xBrick) + (yBrick * yBrick));
                xBrick /= norma;
                yBrick /= norma;

                // Proiezione vettore velocità sulla normale al punto di tangenza
                float dotProduct = (dx * xBrick) + (dy * yBrick);

                // Mi trovo il vettore riflesso
                dx -= 2 * dotProduct * xBrick;
                dy -= 2 * dotProduct * yBrick;

                if (Math.atan(Math.abs(dy / dx)) <= minA) {
                    float speed = (float) Math.sqrt(dx * dx + dy * dy);
                    dx = (float) (Math.signum(dx) * speed * Math.cos(minA));
                    dy = (float) (Math.signum(dy) * speed * Math.sin(minA));
                }

            }
            StartGame.sound.playHitSound();
            return true;
        }
        return false;
    }
}
