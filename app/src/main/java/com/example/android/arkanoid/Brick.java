package com.example.android.arkanoid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;

public class Brick extends View {

    private Bitmap brick; // Contenitore per l'immagine del mattone
    private float x; // Posizione del mattone sull'asse orizzontale
    private float y; // Posizione del mattone sull'asse verticale
    private int lifes; // Vita del Brick

    public Brick(Context context, float x, float y, int lifes, int randomSkin) {
        super(context);
        this.x = x;
        this.y = y;
        this.lifes = lifes;
        skin(randomSkin);
    }

    public Brick(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    // Assegna un colore random al mattone (brick)
    private void skin(int randomSkin) {
        // Random da 0 a 8
        switch (randomSkin) {
            case 0:
                if (lifes > 2) {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_0_3);
                }
                if (lifes == 2) {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_0_2);
                } else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_0_1);
                }
                break;
            case 1:
                if (lifes > 2) {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_1_3);
                }
                if (lifes == 2) {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_1_2);
                } else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_1_1);
                }
                break;
            case 2:
                if (lifes > 2) {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_2_3);
                }
                if (lifes == 2) {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_2_2);
                } else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_2_1);
                }
                break;
            case 3:
                if (lifes > 2) {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_3_3);
                }
                if (lifes == 2) {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_3_2);
                } else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_3_1);
                }
                break;
            case 4:
                if (lifes > 2) {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_4_3);
                }
                if (lifes == 2) {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_4_2);
                } else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_4_1);
                }
                break;
            case 5:
                if (lifes > 2) {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_5_3);
                }
                if (lifes == 2) {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_5_2);
                } else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_5_1);
                }
                break;
            case 6:
                if (lifes > 2) {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_6_3);
                }
                if (lifes == 2) {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_6_2);
                } else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_6_1);
                }
                break;
            case 7:
                if (lifes > 2) {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_7_3);
                }
                if (lifes == 2) {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_7_2);
                } else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_7_1);
                }
                break;
            case 8:
                if (lifes > 2) {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_8_3);
                }
                if (lifes == 2) {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_8_2);
                } else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_8_1);
                }
                break;
        }
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    public int getLifes() {
        return lifes;
    }

    public void setLifes(int lifes) {
        this.lifes = lifes;
    }

    public Bitmap getBrick() {
        return brick;
    }

    public void setImage(int brickLife) {
        Bitmap brickBitmap = getBrick();

        if ((brickBitmap.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.brick_0_2))) || (brickBitmap.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.brick_0_3))) ){
            switch (brickLife) {
                case 1:
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_0_1);
                    break;
                case 2:
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_0_2);
                    break;
            }
        }
        if ((brickBitmap.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.brick_1_2))) || (brickBitmap.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.brick_1_3))) ){
            switch (brickLife) {
                case 1:
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_1_1);
                    break;
                case 2:
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_1_2);
                    break;
            }
        }
        if ((brickBitmap.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.brick_2_2))) || (brickBitmap.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.brick_2_3))) ){
            switch (brickLife) {
                case 1:
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_2_1);
                    break;
                case 2:
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_2_2);
                    break;
            }
        }
        if ((brickBitmap.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.brick_3_2))) || (brickBitmap.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.brick_3_3))) ){
            switch (brickLife) {
                case 1:
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_3_1);
                    break;
                case 2:
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_3_2);
                    break;
            }
        }
        if ((brickBitmap.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.brick_4_2))) || (brickBitmap.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.brick_4_3))) ){
            switch (brickLife) {
                case 1:
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_4_1);
                    break;
                case 2:
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_4_2);
                    break;
            }
        }
        if ((brickBitmap.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.brick_5_2))) || (brickBitmap.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.brick_5_3))) ){
            switch (brickLife) {
                case 1:
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_5_1);
                    break;
                case 2:
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_5_2);
                    break;
            }
        }
        if ((brickBitmap.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.brick_6_2))) || (brickBitmap.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.brick_6_3))) ){
            switch (brickLife) {
                case 1:
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_6_1);
                    break;
                case 2:
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_6_2);
                    break;
            }
        }
        if ((brickBitmap.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.brick_7_2))) || (brickBitmap.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.brick_7_3))) ){
            switch (brickLife) {
                case 1:
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_7_1);
                    break;
                case 2:
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_7_2);
                    break;
            }
        }
        if ((brickBitmap.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.brick_8_2))) || (brickBitmap.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.brick_8_3))) ){
            switch (brickLife) {
                case 1:
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_8_1);
                    break;
                case 2:
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_8_2);
                    break;
            }
        }
    }
}
