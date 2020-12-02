package com.example.android.arkanoid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

public class Game extends View implements SensorEventListener, View.OnTouchListener {

    private Bitmap background;
    private Bitmap redBall;
    private Bitmap stretch;
    private Bitmap flipperBit;

    private Display display;
    private Point size;
    private Paint paint;

    private Ball ball;
    private ArrayList<Brick> brickList;
    private Paddle flipper;

    private RectF r;

    private SensorManager sManager;
    private Sensor accelerometer;

    private int lifes;
    private int score;
    private int level;
    private boolean start;
    private boolean gameOver;
    private Context context;

    public Game(Context context, int lifes, int score) {
        super(context);
        paint = new Paint();

        /*
            Imposto:
            - Contesto
            - Vita
            - Punteggio
            - Livelli
         */
        this.context = context;
        this.lifes = lifes;
        this.score = score;
        level = 0;

        // Imposto il gameOver se il gioco è finito o se il giocatore ha perso
        start = false;
        gameOver = false;

        // Imposto l'accelerometro e il SensorManager
        sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        setBackground(context);

        // Crea un bitmap per la palla e il flipper
        redBall = BitmapFactory.decodeResource(getResources(), R.drawable.redball);
        flipperBit = BitmapFactory.decodeResource(getResources(), R.drawable.flipper);

        // Crea una nuova palla, un nuovo flipper e un nuovo elenco di mattoni
        ball = new Ball(size.x / 2, size.y - 480);
        flipper = new Paddle(size.x / 2, size.y - 400);
        brickList = new ArrayList<Brick>();

        generateBricks(context);
        this.setOnTouchListener(this);

    }

    // Riempi la lista "brickList" con dei mattoni
    private void generateBricks(Context context) {
        for (int i = 3; i < 7; i++) {
            for (int j = 1; j < 6; j++) {
                brickList.add(new Brick(context, j * 150, i * 100));
            }
        }
    }

    // Imposta sfondo
    private void setBackground(Context context) {
        background = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.background));
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        size = new Point();
        display.getSize(size);
    }

    protected void onDraw(Canvas canvas) {
        // Imposta lo sfondo solamente una volta
        if (stretch == null) {
            stretch = Bitmap.createScaledBitmap(background, size.x, size.y, false);
        }
        canvas.drawBitmap(stretch, 0, 0, paint);

        // Disegna la palla
        paint.setColor(Color.RED);
        canvas.drawBitmap(redBall, ball.getX(), ball.getY(), paint);

        // Disegna il flipper
        paint.setColor(Color.WHITE);
        r = new RectF(flipper.getX(), flipper.getY(), flipper.getX() + 200, flipper.getY() + 40);
        canvas.drawBitmap(flipperBit, null, r, paint);

        // Disegna i mattoni
        paint.setColor(Color.GREEN);
        for (int i = 0; i < brickList.size(); i++) {
            Brick b = brickList.get(i);
            r = new RectF(b.getX(), b.getY(), b.getX() + 100, b.getY() + 80);
            canvas.drawBitmap(b.getBrick(), null, r, paint);
        }

        // Disegna il testo
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        canvas.drawText("" + lifes, 400, 100, paint);
        canvas.drawText("" + score, 700, 100, paint);

        // In caso di sconfitta, scrivi "Game over!"
        if (gameOver) {
            paint.setColor(Color.RED);
            paint.setTextSize(100);
            canvas.drawText("Game over!", size.x / 4, size.y / 2, paint);
        }
    }

    // Controlla che la palla non tocchi i bordi (Edges)
    private void checkEdges() {
        if (ball.getX() + ball.getxSpeed() >= size.x - 60) {
            ball.changeDirection("prava");
        } else if (ball.getX() + ball.getxSpeed() <= 0) {
            ball.changeDirection("lava");
        } else if (ball.getY() + ball.getySpeed() <= 150) {
            ball.changeDirection("hore");
        } else if (ball.getY() + ball.getySpeed() >= size.y - 200) {
            checkLifes();
        }
    }

    // Controlla lo stato del gioco.
    // Check sulle vite disponibili
    // oppure se il gioco è terminato.
    private void checkLifes() {
        if (lifes == 1) {
            gameOver = true;
            start = false;
            invalidate();
        } else {
            lifes--;
            ball.setX(size.x / 2);
            ball.setY(size.y - 480);
            ball.generateSpeed();
            ball.raiseSpeed(level);
            start = false;
        }
    }

    // Ad ogni passaggio, controlla se
    // ci sia stata o una collisione,
    // una vittoria o una sconfitta, etc.
    public void update() {
        if (start) {
            win();
            checkEdges();
            ball.hitFlipper(flipper.getX(), flipper.getY());
            for (int i = 0; i < brickList.size(); i++) {
                Brick b = brickList.get(i);
                if (ball.hitBrick(b.getX(), b.getY())) {
                    brickList.remove(i);
                    score = score + 80;
                }
            }
            ball.move();
        }
    }

    public void pauseGame() {
        sManager.unregisterListener(this);
    }

    public void resumeGame() {
        sManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    // Comanda Flipper tramite accelerometro
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            flipper.setX(flipper.getX() - event.values[0] - event.values[0]);

            if (flipper.getX() + event.values[0] > size.x - 240) {
                flipper.setX(size.x - 240);
            } else if (flipper.getX() - event.values[0] <= 20) {
                flipper.setX(20);
            }
        }
    }

    // Metodo necessario solamente perchè la classe Game
    // implementa SensorEventListener, la quale necessita
    // dell'override del metodo astratto onAccuracyChanged
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // Serve a sospendere il gioco in caso di una nuova partita
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (gameOver == true && start == false) {
            score = 0;
            lifes = 3;
            resetLevel();
            gameOver = false;

        } else {
            start = true;
        }
        return false;
    }

    // Imposta la partita per iniziare
    private void resetLevel() {
        ball.setX(size.x / 2);
        ball.setY(size.y - 480);
        ball.generateSpeed();
        brickList = new ArrayList<Brick>();
        generateBricks(context);
    }

    // Check se il giocatore ha vinto o meno
    private void win() {
        if (brickList.isEmpty()) {
            ++level;
            resetLevel();
            ball.raiseSpeed(level);
            start = false;
        }
    }
}
