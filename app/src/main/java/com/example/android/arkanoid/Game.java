package com.example.android.arkanoid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Random;

public class Game extends View implements SensorEventListener, View.OnTouchListener {

    private ArrayList<Brick> brickList;
    private ArrayList<PowerUp> powerUpList;

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

    private Random rand;
    private RectF r;
    private RectF rect;

    private Sensor accelerometer;
    private SensorManager sManager;

    private boolean gameOver;
    private boolean ignore;
    private boolean newGame;
    private boolean newGameStarted;
    private boolean start;
    private boolean touchSensor;
    private boolean powerUpTaken;
    private boolean powerUpTakenAtLeastOneTime;
    private boolean powerUpSkippedAtThisLevel;

    private float xBall;
    private float xFlipper;
    private float yBall;
    private float yFlipper;
    private float xPowerUp;
    private float yPowerUp;

    private int level;
    private int lifes;
    private int score;
    private int numberOfPowerUps;
    private int numberOfPowerUpsTaken;
    private int p = 1;

    public Game(Context context, int lifes, int score) {
        super(context);
        paint = new Paint();
        textPaint = new Paint();
        rand = new Random();

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

        // Controllo lo stato della Switch tramite getSharedPreferences
        // @param save = Il nome della SharedPreferences
        // @param value = L'ID del Boolean della switch
        SharedPreferences mPrefs = context.getSharedPreferences("save", 0);
        touchSensor = mPrefs.getBoolean("value", true);

        setBackground(context);

        // Crea un bitmap per la palla e il flipper
        redBall = BitmapFactory.decodeResource(getResources(), R.drawable.redball);
        flipperBit = BitmapFactory.decodeResource(getResources(), R.drawable.flipper);

        /*
            Crea una nuova palla, un nuovo flipper, un nuovo elenco di mattoni
            e un nuovo elenco di PowerUp
         */

        // Palla
        xBall = (float) (size.x / 2) - 30;
        yBall = (float) (size.y - 480);
        ball = new Ball(xBall , yBall);

        // Flipper
        xFlipper = (float) (size.x / 2) - 90;
        yFlipper = (float) (size.y - 390);
        flipper = new Flipper(xFlipper , yFlipper);

        // PowerUps
        int min = 100;
        int max = 750;
        int s = 0;
        numberOfPowerUpsTaken = 0;
        xPowerUp = (int) (Math.random() * 950);
        while (s == 0) {
            yPowerUp = rand.nextInt(max - min + 1) + min;
            if (yPowerUp < 221 || yPowerUp > 690) {
                s = 1;
            }
        }
        powerUpList = new ArrayList<PowerUp>();
        generatePowerUps(context);

        // Mattoni
        brickList = new ArrayList<Brick>();
        generateBricks(context);

        this.setOnTouchListener(this);

    }

    // Riempi la lista "powerUpList" con dei powerup
    private void generatePowerUps(Context context) {
        numberOfPowerUps = 1;
        if (powerUpTakenAtLeastOneTime && numberOfPowerUpsTaken >= 1 || (powerUpSkippedAtThisLevel))  {
            int min = 100;
            int max = 750;
            int s = 0;
            xPowerUp = (int) (Math.random() * 950);
            while (s == 0) {
                yPowerUp = rand.nextInt(max - min + 1) + min;
                if (yPowerUp < 221 || yPowerUp > 690) {
                    s = 1;
                }
            }
            for (int i = 0; i < numberOfPowerUps; i++) {
                powerUpList.add(new PowerUp(context, xPowerUp, yPowerUp));
            }
        } else {
            for (int i = 0; i < numberOfPowerUps; i++) {
                powerUpList.add(new PowerUp(context, xPowerUp, yPowerUp));
            }
        }
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
        RectF FlipperRect = new RectF();
        r = new RectF();

        // Margine per evitare che il Paddle tocchi il bordo del Canvas/View
        // Si può impostare a 0, in modo tale che tocchi proprio il bordo del Canvas/View
        final int canvasMargin = 5;

        // Scelgo la dimensione fissa dell'Height del Flipper
        final int flipperHeight = 40;
        int flipperWidth;

        // Scelgo la larghezza del Flipper in base al PowerUp
        if (powerUpTaken) {
            flipperWidth = 350;
        }
        else {
            flipperWidth = 200;
        }

        // Calcolo il RectF del Flipper (ma attenzione - potrebbe essere fuori schermo, quindi devo correggerlo subito dopo)
        FlipperRect.left = Math.max(canvasMargin, flipper.getX());
        FlipperRect.right = FlipperRect.left + flipperWidth;

        // Verifico, quindi, se sto uscendo fuori dallo schermo dalla parte destra
        // Se sì, correggo la larghezza.
        // Inoltre setto il Top e il Bottom
        if (FlipperRect.right > (this.getWidth() - canvasMargin)) {
            FlipperRect.left = (this.getWidth() - canvasMargin) - flipperWidth;
            FlipperRect.right = FlipperRect.left + flipperWidth;
        }

        FlipperRect.top = Math.max(0, flipper.getY());
        FlipperRect.bottom = FlipperRect.top + flipperHeight;

        // Costruisco, infine, il Flipper
        r.set(FlipperRect);

        if (newGameStarted) {
            flipper.setX(xFlipper);
        }
        canvas.drawBitmap(flipperBit, null, r, paint);

        // Disegna i mattoni
        paint.setColor(Color.GREEN);
        for (int i = 0; i < brickList.size(); i++) {
            Brick b = brickList.get(i);
            r = new RectF(b.getX(), b.getY(), b.getX() + 100, b.getY() + 80);
            canvas.drawBitmap(b.getBrick(), null, r, paint);
        }

        // Disegna i powerup
        if (!powerUpTakenAtLeastOneTime && level < 1) {
            if (score >= 1280) {
                paint.setColor(Color.GREEN);
                for (int i = 0; i < powerUpList.size(); i++) {
                    PowerUp mIsPowerUp = powerUpList.get(i);
                    rect = new RectF(mIsPowerUp.getX(), mIsPowerUp.getY(), mIsPowerUp.getX() + 100, mIsPowerUp.getY() + 80);
                    canvas.drawBitmap(mIsPowerUp.getPowerUp(), null, rect, paint);
                }
            }
        } else if (!powerUpTakenAtLeastOneTime && level >= 1) {
            if (score >= (1600 * level) + 1280) {
                paint.setColor(Color.GREEN);
                for (int i = 0; i < powerUpList.size(); i++) {
                    PowerUp mIsPowerUp = powerUpList.get(i);
                    rect = new RectF(mIsPowerUp.getX(), mIsPowerUp.getY(), mIsPowerUp.getX() + 100, mIsPowerUp.getY() + 80);
                    canvas.drawBitmap(mIsPowerUp.getPowerUp(), null, rect, paint);
                }
            }
        }
        if (powerUpTakenAtLeastOneTime) {
            if (score >= (1600 * level) + (numberOfPowerUpsTaken * 500) + 1280) {
                paint.setColor(Color.GREEN);
                for (int i = 0; i < powerUpList.size(); i++) {
                    PowerUp mIsPowerUp = powerUpList.get(i);
                    rect = new RectF(mIsPowerUp.getX(), mIsPowerUp.getY(), mIsPowerUp.getX() + 100, mIsPowerUp.getY() + 80);
                    canvas.drawBitmap(mIsPowerUp.getPowerUp(), null, rect, paint);
                }
            }
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

        newGameStarted = false;

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
                newGameStarted = true;
                canvas.drawText("Tocca per iniziare", xPos, yPos, paint);
            }
        }
    }

    // Controlla che la palla non tocchi i bordi (Edges)
    private void checkEdges() {
        if (ball.getX() + ball.getxSpeed() >= size.x - 60) {
            StartGame.sound.playHitSound();
            ball.changeDirection("prava");
        } else if (ball.getX() + ball.getxSpeed() <= 0) {
            StartGame.sound.playHitSound();
            ball.changeDirection("lava");
        } else if (ball.getY() + ball.getySpeed() <= 150) {
            StartGame.sound.playHitSound();
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
            ignore = true;

            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setTitle("Game over!");
            alertDialog.setMessage("Vuoi iniziare una nuova partita?");

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sì", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    newGame = true;
                    ignore = false;
                    // Termina l'attività del gioco e iniziane un'altra
                    StartGame.activity.finish();
                    Intent intent = new Intent(context,StartGame.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
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
            checkPowerUp(score, level, powerUpTakenAtLeastOneTime, numberOfPowerUpsTaken);

            for (int i = 0; i < brickList.size(); i++) {
                Brick b = brickList.get(i);
                if (ball.hitBrick(b.getX(), b.getY())) {
                    brickList.remove(i);
                    score = score + 80;
                }
            }

            if (score >= 1000 * p) {
                StartGame.sound.playScoreSound();
                p++;
            }

            ball.move();
        }
    }

    private void checkPowerUp(int mScore, int mLevel, boolean mPowerUpTakenAtLeastOneTime, int mNumberOfPowerUpsTaken) {
        if (!mPowerUpTakenAtLeastOneTime && mLevel < 1) {
            if (mScore >= 1280) {
                for (int i = 0; i < powerUpList.size(); i++) {
                    PowerUp p = powerUpList.get(i);
                    if (ball.hitPowerUp(p.getX(), p.getY())) {
                        powerUpList.remove(i);
                        powerUpTaken = true;
                        powerUpTakenAtLeastOneTime = true;
                        numberOfPowerUpsTaken++;
                        score = score + 500;
                    }
                }
            }
        } else if (!mPowerUpTakenAtLeastOneTime && mLevel >= 1) {
            if (mScore >= (1600 * mLevel) + 1280) {
                for (int i = 0; i < powerUpList.size(); i++) {
                    PowerUp p = powerUpList.get(i);
                    if (ball.hitPowerUp(p.getX(), p.getY())) {
                        powerUpList.remove(i);
                        powerUpTaken = true;
                        powerUpTakenAtLeastOneTime = true;
                        numberOfPowerUpsTaken++;
                        score = score + 500;
                    }
                }
            }
        }
        if (mPowerUpTakenAtLeastOneTime) {
            if (mScore >= (1600 * mLevel) + (mNumberOfPowerUpsTaken * 500) + 1280) {
                for (int i = 0; i < powerUpList.size(); i++) {
                    PowerUp p = powerUpList.get(i);
                    if (ball.hitPowerUp(p.getX(), p.getY())) {
                        powerUpList.remove(i);
                        powerUpTaken = true;
                        powerUpTakenAtLeastOneTime = true;
                        numberOfPowerUpsTaken++;
                        score = score + 500;
                    }
                }
            }
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
        if(!touchSensor) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                flipper.setX(flipper.getX() - event.values[0] - event.values[0]);

                if (flipper.getX() + event.values[0] > size.x - 200) {
                    flipper.setX(size.x - 200);
                } else if (flipper.getX() - event.values[0] <= 10) {
                    flipper.setX(10);
                }
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
        if (ignore) {
            return false;
        } else if (gameOver == true && start == false) {
            score = 0;
            lifes = 3;
            resetLevel();
            p = 1;
            gameOver = false;
            return false;
        } else {
            final int action = event.getAction();
                switch (action & MotionEvent.ACTION_MASK) {
                    // Nel caso in cui si rilevano dei movimenti (touch)
                    // sull'asse x, prendo questo dato e lo imposto
                    // come il punto x del Flipper
                    case MotionEvent.ACTION_MOVE:{
                        if (!touchSensor) {
                            break;
                        }
                        float x = event.getX();
                        // Se l'utente porta il dito oltre la posizione desiderata
                        // allora la impostiamo noi in modo tale che il Flipper non esca
                        if (x >= size.x - 202) {
                            event.setLocation(size.x - 200, flipper.getY());
                        } else {
                            flipper.setX(x);
                        }
                        break;
                    }
                }
            start = true;
            ignore = false;
            return true;
        }
    }

    // Imposta la partita per iniziare
    private void resetLevel() {
        newGame = false;
        ball.setX(xBall);
        ball.setY(yBall);
        ball.generateSpeed();
        powerUpList = new ArrayList<PowerUp>();
        brickList = new ArrayList<Brick>();
        generateBricks(context);
        generatePowerUps(context);
        powerUpTaken = false;
        powerUpSkippedAtThisLevel = false;
        ignore = false;
    }

    // Check se il giocatore ha vinto o meno
    private void win() {
        if (!powerUpList.isEmpty() && brickList.isEmpty()) {
            powerUpSkippedAtThisLevel = true;
        }

        if (brickList.isEmpty()) {
            ++level;
            resetLevel();
            ball.raiseSpeed(level);
            start = false;
        }
    }
}
