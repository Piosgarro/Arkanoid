package com.example.android.arkanoid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

public class Game extends View implements SensorEventListener, View.OnTouchListener {

    private ArrayList<Brick> brickList;
    
    private Ball ball;
    
    private Bitmap background;
    private Bitmap flipperBit;
    private Bitmap redBall;
    private Bitmap stretch;
    
    private Context context;
    
    private Display display;
    
    private Flipper flipper;
    
    private Paint life1;
    private Paint life2;
    private Paint life3;
    private Paint paint;
    private Paint textPaint;
    
    private Point size;
    
    private RectF r;
    
    private Sensor accelerometer;
    private SensorManager sManager;
    
    private boolean gameOver;
    private boolean newGame;
    private boolean start;

    private float xBall;
    private float xFlipper;
    private float yBall;
    private float yFlipper;

    private int level;
    private int lifes;
    private int score;

    public Game(Context context, int lifes, int score) {
        super(context);
        paint = new Paint();
        textPaint = new Paint();

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
        xBall = (float) (size.x / 2) - 30;
        yBall = (float) (size.y - 480);
        xFlipper = (float) (size.x / 2) - 90;
        yFlipper = (float) (size.y - 390);

        ball = new Ball(xBall , yBall);
        flipper = new Flipper(xFlipper , yFlipper);
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
        Typeface candalFont = ResourcesCompat.getFont(context, R.font.candal);

        life1 = new Paint();
        life2 = new Paint();
        life3 = new Paint();

        life1.setColor(Color.WHITE);
        life1.setTextSize(50);
        life1.setTypeface(candalFont);

        life2.setColor(Color.WHITE);
        life2.setTextSize(50);
        life2.setTypeface(candalFont);

        life3.setColor(Color.WHITE);
        life3.setTextSize(50);
        life3.setTypeface(candalFont);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(80);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.create(candalFont, Typeface.ITALIC));

        canvas.drawText("Livello: " + level, (canvas.getWidth() / 2), 80, textPaint);

        switch(lifes) {
            case 1:
                canvas.drawText("\uD83D\uDC9B", 110, 1670, life1);
                break;
            case 2:
                canvas.drawText("\uD83D\uDC9B", 110, 1670, life1);
                canvas.drawText("\uD83D\uDC9B", 170, 1670, life2);
                break;
            case 3:
                canvas.drawText("\uD83D\uDC9B", 110, 1670, life1);
                canvas.drawText("\uD83D\uDC9B", 170, 1670, life2);
                canvas.drawText("\uD83D\uDC9B", 230, 1670, life3);
                break;
            default:
                break;
        }

        canvas.drawText("" + score, (canvas.getWidth() / 2), 1370, textPaint);

        // In caso di sconfitta, scrivi "Game over!"
        if (gameOver) {
            // Imposto un nuovo carattere
            // e centro il testo
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.ITALIC));

            // Divido la larghezza totale dello schermo per 2
            int xPos = (canvas.getWidth() / 2);

            // Divido l'altezza totale dello schermo per 2
            // e la sottraggo per la distanza che c'è al di sopra del testo
            // più la distanza che c'è al di sotto del testo.
            // In questo modo, insieme a "xPos", avrò il testo centrato perfettamente
            int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;

            paint.setColor(Color.WHITE);
            paint.setTextSize(80);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(Typeface.create(candalFont, Typeface.ITALIC));
            if (newGame) {
                canvas.drawText("Tocca per iniziare", xPos, yPos, paint);
            }
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

            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Game over!");
            alertDialog.setMessage("Vuoi iniziare una nuova partita?");

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sì", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    newGame = true;
                    invalidate();
                }
            });

            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    newGame = false;
                    // Termina l'attività del gioco (automaticamente torna al Menù)
                    StartGame.activity.finish();
                }
            });

            alertDialog.show();

        } else {
            lifes--;
            ball.setX(xBall - 8);
            ball.setY(yBall);
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

            if (flipper.getX() + event.values[0] > size.x - 200) {
                flipper.setX(size.x - 200);
            } else if (flipper.getX() - event.values[0] <= 10) {
                flipper.setX(10);
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
        newGame = false;
        ball.setX(xBall);
        ball.setY(yBall);
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
