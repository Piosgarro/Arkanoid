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
import android.os.CountDownTimer;
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

    private CountDownTimer ballPowerUpTimer;

    private final Ball ball;
    private Ball ball_1;
    private Ball ball_2;

    private Bitmap background;
    private final Bitmap flipperBit;
    private final Bitmap redBall;
    private Bitmap stretch;

    private final Context context;

    private final Flipper flipper;

    private final Paint paint;
    private final Paint textPaint;

    private Point size;

    private PowerUp mIsPowerUp;

    private final Random rand;

    private final Sensor accelerometer;
    private final SensorManager sManager;

    private boolean gameOver;
    private boolean ignore;
    private boolean newGameStarted;
    private boolean powerUpSkippedAtThisLevel;
    private boolean flipperPowerUpTaken;
    private boolean flipperPowerDownTaken;
    private boolean powerUpTakenAtLeastOneTime;
    private boolean powerUpIsNotAlive;
    private boolean start;
    private boolean shouldSkipTimer;
    private boolean timer1Ended;
    private boolean powerUpGone;
    private boolean ballPowerUpTaken;
    private boolean ball1NotVisible;
    private boolean ball2NotVisible;
    private final boolean touchSensor;

    private final float xBall;
    private final float yBall;
    private final float xFlipper;
    private float xPowerUp;
    private float yPowerUp;

    private int flipperWidth;
    private int level;
    private int lifes;
    private int numberOfPowerUpsTaken;
    private int p = 1;
    private int score;
    private long seconds;
    private int index;

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

        // Controllo lo stato della Switch sul Touch tramite getSharedPreferences
        // @param save = Il nome della SharedPreferences
        // @param valueTouch = L'ID del Boolean della switch
        SharedPreferences mPrefs = context.getSharedPreferences("save", 0);
        touchSensor = mPrefs.getBoolean("valueTouch", true);

        setBackground(context);

        // Crea un bitmap per la palla e il flipper
        redBall = BitmapFactory.decodeResource(getResources(), R.drawable.redball);
        flipperBit = BitmapFactory.decodeResource(getResources(), R.drawable.flipper);

        /*
            Crea una nuova palla, un nuovo flipper, un nuovo elenco di mattoni
            e un nuovo elenco di PowerUp
         */

        // Palla
        xBall = (float) (size.x / 2) - 30; // Posizione della palla sull'asse orizzontale
        yBall = (float) (size.y - 480); // Posizione della palla sull'asse verticale
        ball = new Ball(xBall , yBall);

        // Flipper
        xFlipper = (float) (size.x / 2) - 90; // Posizione del Flipper sull'asse orizzontale
        float yFlipper = (float) (size.y - 390); // Posizione del Flipper sull'asse verticale
        flipper = new Flipper(xFlipper , yFlipper);

        // PowerUps
        int min = 100; // Posizione minima del PowerUp sull'asse verticale
        int max = 750; // Posizione massima del PowerUp sull'asse orizzontale
        int s = 0; // Contatore
        numberOfPowerUpsTaken = 0; // Contatore dei PowerUp
        xPowerUp = (int) (Math.random() * 950); // Posizione del PowerUp sull'asse orizzontale
        while (s == 0) {
            yPowerUp = rand.nextInt(max - min + 1) + min; // Genera un numero random tra 100 e 750
            if (yPowerUp < 221 || yPowerUp > 690) { // Se il numero è < 221 o > 690 significa che è stato
                s = 1;                              // generato un numero che non ci serviva, quindi ricalcolo.
            }
        }
        seconds = rand.nextInt(25000 - 7000 + 1) + 7000;
        powerUpList = new ArrayList<>();
        generatePowerUps(context);

        // Mattoni
        brickList = new ArrayList<>();
        generateBricks(context);

        this.setOnTouchListener(this);

    }

    // Riempi la lista "powerUpList" con dei powerup
    private void generatePowerUps(Context context) {
        int numberOfPowerUps = 3; // Numero dei PowerUp

        powerUpList.clear(); // Svuotiamo l'eventuale ArrayList

        // Se ho preso il powerUp almeno una volta oppure se il powerUp è stato skippato
        // nel livello seguente, allora rigenero il powerUp in una posizione random (nuovamente)
        // e lo aggiungo all'ArrayList, il quale mi servirà dopo per poter "disegnare" i powerUp sullo schermo
        // ------------------------------------------------------------------------------------
        // Imposto questa "if" per evitare che un powerUp venga aggiunto nella stessa posizione di un
        // powerUp già presente.
        if (powerUpTakenAtLeastOneTime && numberOfPowerUpsTaken >= 1 || (powerUpSkippedAtThisLevel))  {
            int min = 100;
            int max = 750;
            int s = 0;
            for (int i = 0; i < numberOfPowerUps; i++) {
                xPowerUp = (int) (Math.random() * 950);
                while (s == 0) {
                    yPowerUp = rand.nextInt(max - min + 1) + min;
                    if (yPowerUp < 221 || yPowerUp > 690) {
                        s = 1;
                    }
                }
                powerUpList.add(new PowerUp(context, xPowerUp, yPowerUp));
            }
        } else {
            int min = 100;
            int max = 750;
            int s = 0;
            for (int i = 0; i < numberOfPowerUps; i++) {
                xPowerUp = (int) (Math.random() * 950);
                while (s == 0) {
                    yPowerUp = rand.nextInt(max - min + 1) + min;
                    if (yPowerUp < 221 || yPowerUp > 690) {
                        s = 1;
                    }
                }
                powerUpList.add(new PowerUp(context, xPowerUp, yPowerUp));
            }
        }
        // define the range
        int max = 2;
        int min = 0;
        int range = max - min + 1;
        index = (int)(Math.random() * range) + min;
        Log.d("D:","PowerUp Random Index: " + Integer.toString(index));
        mIsPowerUp = powerUpList.get(index);
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
        Display display = wm.getDefaultDisplay();
        size = new Point();
        display.getSize(size); // Prendi le dimensione dello schermo
    }

    protected void onDraw(Canvas canvas) {
        // Imposta lo sfondo solamente una volta
        if (stretch == null) {
            stretch = Bitmap.createScaledBitmap(background, size.x, size.y, false);
        }
        canvas.drawBitmap(stretch, 0, 0, paint); // Disegna il background sul Canvas

        // Disegna la palla
        paint.setColor(Color.RED);
        canvas.drawBitmap(redBall, ball.getX(), ball.getY(), paint); // Disegna la palla sul Canvas, nelle posizioni specificate precedentemente
        if (ballPowerUpTaken) {
            // Disegna la palla relativa al powerUp
            if (!ball1NotVisible) {
                canvas.drawBitmap(redBall, ball_1.getX(), ball_1.getY(), paint); // Disegna la palla sul Canvas, nelle posizioni specificate precedentemente
            }
            if (!ball2NotVisible) {
                canvas.drawBitmap(redBall, ball_2.getX(), ball_2.getY(), paint); // Disegna la palla sul Canvas, nelle posizioni specificate precedentemente
            }
        }

        // Disegna il flipper
        RectF FlipperRect = new RectF();
        RectF r = new RectF();

        // Margine per evitare che il Paddle tocchi il bordo del Canvas/View
        // Si può impostare a 0, in modo tale che tocchi proprio il bordo del Canvas/View
        final int canvasMargin = 5;

        // Scelgo la dimensione fissa dell'Height del Flipper
        final int flipperHeight = 40;

        // Scelgo la larghezza del Flipper in base al PowerUp
        if (flipperPowerUpTaken) {
            flipperWidth = 350;
        }
        else if (flipperPowerDownTaken) {
            flipperWidth = 150;
        } else {
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

        // Se sto iniziando un nuovo livello, oppure sto iniziando da capo
        // imposto il Flipper nella posizione ottimale
        if (newGameStarted) {
            flipper.setX(xFlipper);
        }

        canvas.drawBitmap(flipperBit, null, r, paint); // Disegna il Flipper sul Canvas

        // Disegna i mattoni sul Canvas, prendendo ogni oggetto dall'ArrayList
        // e assegnandoli un rettangolo dalle dimensioni specificate in generateBricks()
        paint.setColor(Color.GREEN);
        for (int i = 0; i < brickList.size(); i++) {
            Brick b = brickList.get(i);
            r = new RectF(b.getX(), b.getY(), b.getX() + 100, b.getY() + 80);
            canvas.drawBitmap(b.getBrick(), null, r, paint);
        }

        if (!shouldSkipTimer) {
            new CountDownTimer(seconds, 1000) {

                public void onTick(long millisUntilFinished) {
                    shouldSkipTimer = true;
                }

                public void onFinish() {
                    timer1Ended = true;
                    new CountDownTimer(seconds, 1000) {
                        public void onTick(long millisUntilFinished) {
                            powerUpGone = false;
                        }

                        public void onFinish() {
                            powerUpGone = true;
                        }
                    }.start();
                }
            }.start();
        }

        // Disegna i powerUp sul Canvas, prendendo ogni oggetto dall'ArrayList
        // e assegnandoli un rettangolo dalle dimensioni specificate in generatePowerUps()
        RectF rect;

        if (timer1Ended && !powerUpGone && !powerUpIsNotAlive) {
            paint.setColor(Color.GREEN);
            rect = new RectF(mIsPowerUp.getX(), mIsPowerUp.getY(), mIsPowerUp.getX() + 100, mIsPowerUp.getY() + 80);
            canvas.drawBitmap(mIsPowerUp.getPowerUp(), null, rect, paint);
        }

        // Disegna il testo
        Typeface candalFont = ResourcesCompat.getFont(context, R.font.candal);

        Paint life = new Paint(); // Risorsa che utilizzo per scrivere qualcosa

        life.setTextSize(50); // Imposta la dimensione del carattere
        life.setTypeface(candalFont); // Imposta il Font (R.font.candal)

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(80);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.create(candalFont, Typeface.ITALIC));

        // Scrivi "Livello: / Level: " nella posizione specificata
        canvas.drawText(getContext().getString(R.string.level) + level, ((float) getWidth() / 2), 80, textPaint);

        // In base a quante vite ho al momento, scrivi 3, 2 o 1 cuore nella posizione specificata
        switch(lifes) {
            case 1:
                canvas.drawText("\uD83D\uDC9B", 110, 1670, life);
                break;
            case 2:
                canvas.drawText("\uD83D\uDC9B", 110, 1670, life);
                canvas.drawText("\uD83D\uDC9B", 170, 1670, life);
                break;
            case 3:
                canvas.drawText("\uD83D\uDC9B", 110, 1670, life);
                canvas.drawText("\uD83D\uDC9B", 170, 1670, life);
                canvas.drawText("\uD83D\uDC9B", 230, 1670, life);
                break;
            default:
                break;
        }

        // Punteggio al centro dello schermo
        canvas.drawText("" + score, ((float) getWidth() / 2), 1370, textPaint);

        // Reimposto il boolean (riguardante se ho iniziato o meno un nuovo gioco) su falso,
        // poichè ci sto giocando.
        newGameStarted = false;
    }

    // Controlla che la palla non tocchi i bordi (Edges)
    private void checkEdges() {
        if (!ballPowerUpTaken) {
            if (ball.getX() + redBall.getWidth() >= size.x) {
                StartGame.sound.playHitSound();
                ball.invertXSpeed();
            } else if (ball.getX() <= 0) {
                StartGame.sound.playHitSound();
                ball.invertXSpeed();
            } else if (ball.getY() <= 150) {
                StartGame.sound.playHitSound();
                ball.invertYSpeed();
            } else if(ball.getY() > flipper.getY() + 100) {
                checkLifes();
            } else if ((ball.getY() + redBall.getHeight()) > flipper.getY()) {
                float ballX = ball.getX();
                ballX += (float) redBall.getWidth() / 2;
                if ((ballX > flipper.getX()) && (ballX < (flipper.getX() + flipperWidth))) {
                    StartGame.sound.playHitSound();
                    ball.invertYSpeed();
                }
            }
        } else {
            if (ball.getX() + redBall.getWidth() >= size.x) {
                StartGame.sound.playHitSound();
                ball.invertXSpeed();
            } else if (ball.getX() <= 0) {
                StartGame.sound.playHitSound();
                ball.invertXSpeed();
            } else if (ball.getY() <= 150) {
                StartGame.sound.playHitSound();
                ball.invertYSpeed();
            } else if(ball.getY() > flipper.getY() + 100) {
                checkLifes();
            } else if ((ball.getY() + redBall.getHeight()) > flipper.getY()) {
                float ballX = ball.getX();
                ballX += (float) redBall.getWidth() / 2;
                if ((ballX > flipper.getX()) && (ballX < (flipper.getX() + flipperWidth))) {
                    StartGame.sound.playHitSound();
                    ball.invertYSpeed();
                }
            }
            if (ball_1.getX() + redBall.getWidth() >= size.x) {
                StartGame.sound.playHitSound();
                ball_1.invertXSpeed();
            } else if (ball_1.getX() <= 0) {
                StartGame.sound.playHitSound();
                ball_1.invertXSpeed();
            } else if (ball_1.getY() <= 150) {
                StartGame.sound.playHitSound();
                ball_1.invertYSpeed();
            } else if(ball_1.getY() > flipper.getY() + 100) {
                checkLifes();
            } else if ((ball_1.getY() + redBall.getHeight()) > flipper.getY()) {
                float ballX = ball_1.getX();
                ballX += (float) redBall.getWidth() / 2;
                if ((ballX > flipper.getX()) && (ballX < (flipper.getX() + flipperWidth))) {
                    StartGame.sound.playHitSound();
                    ball_1.invertYSpeed();
                }
            }
            if (ball_2.getX() + redBall.getWidth() >= size.x) {
                StartGame.sound.playHitSound();
                ball_2.invertXSpeed();
            } else if (ball_2.getX() <= 0) {
                StartGame.sound.playHitSound();
                ball_2.invertXSpeed();
            } else if (ball_2.getY() <= 150) {
                StartGame.sound.playHitSound();
                ball_2.invertYSpeed();
            } else if(ball_2.getY() > flipper.getY() + 100) {
                checkLifes();
            } else if ((ball_2.getY() + redBall.getHeight()) > flipper.getY()) {
                float ballX = ball_2.getX();
                ballX += (float) redBall.getWidth() / 2;
                if ((ballX > flipper.getX()) && (ballX < (flipper.getX() + flipperWidth))) {
                    StartGame.sound.playHitSound();
                    ball_2.invertYSpeed();
                }
            }
        }
    }

    // Controlla lo stato del gioco.
    // Check sulle vite disponibili
    // oppure se il gioco è terminato.
    private void checkLifes() {

        // Caso in cui hai perso
        if (lifes == 1) {
            StartGame.sound.playLostLife(); // Se il gioco è finito, attiva il suono relativo

            // Reimposto le variabili
            gameOver = true;
            start = false;
            ignore = true; // Ignoro il touch input dell'Utente

            // Creo un oggetto AlertDialog, che mi permette di visualizzare
            // un dialog riguardo la scelta di iniziare una nuova partita o meno
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setTitle(getContext().getString(R.string.gameOver));
            alertDialog.setMessage(getContext().getString(R.string.startNewMatch));

            // Codice se l'utente clicca "Si"
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Reimposto la variabile riguardante il Touch.
                    // L'utente ora vuole utilizzare il Touch per giocare.
                    ignore = false;

                    // Termina l'attività del gioco e iniziane un'altra
                    StartGame.activity.finish();
                    Intent intent = new Intent(context,StartGame.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getContext().getString(R.string.no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Termina l'attività del gioco (automaticamente torna al Menù)
                    StartGame.activity.finish();
                }
            });

            alertDialog.show();

        // Caso in cui l'utente ha più di una vita
        } else {
            if (ballPowerUpTaken) {
                ballPowerUpTimer.cancel();
                ballPowerUpTaken = false;
            }
            lifes--;
            StartGame.sound.playLostLife(); // Se l'utente perde una vita, attiva il suono relativo
            ball.setX(xBall - 8); // Reimposta la palla al centro dello schermo
            ball.setY(yBall); // Reimposta la palla al centro dello schermo
            ball.generateSpeed();
            ball.raiseSpeed(level);
            start = false; // Inizializzo start a false, poichè l'utente non ha ancora "iniziato" a giocare
        }
    }

    // Ad ogni passaggio, controlla se
    // ci sia stata o una collisione,
    // una vittoria o una sconfitta, etc.
    public void update() {
        if (start) {
            win(); // Controlla se l'utente ha vinto
            checkEdges(); // Controlla se la palla ha toccato i bordi / Flipper
            checkPowerUp(timer1Ended, powerUpGone, powerUpIsNotAlive); // Controlla se l'utente ha preso un powerUp

            // Prendo la lista dei mattoni e controllo se la palla ha colpito un mattone.
            // Se sì, allora rimuovo il mattone dall'ArrayList e aggiorno lo score
            for (int i = 0; i < brickList.size(); i++) {
                Brick b = brickList.get(i);
                if (ball.hitBrick(b.getX(), b.getY())) {
                    brickList.remove(i);
                    score = score + 80;
                }

                if (ballPowerUpTaken) {
                    if (ball_1.hitBrick(b.getX(), b.getY())) {
                        brickList.remove(i);
                        score = score + 80;
                    }
                    if (ball_2.hitBrick(b.getX(), b.getY())) {
                        brickList.remove(i);
                        score = score + 80;
                    }
                }
            }

            // Se l'utente ha raggiunto 1000/2000/3000/4000/x000 ecc.
            // attivo il suono relativo
            if (score >= 1000 * p) {
                StartGame.sound.playScoreSound();
                p++;
            }

            ball.move();
            if (ballPowerUpTaken) {
                ball_1.move();
                ball_2.move();
            }
        }
    }

    // Il controllo sul powerUp, segue la stessa logica utilizzata per la generazione degli stessi.
    private void checkPowerUp(boolean timerFinished, boolean powerUpIsGone, boolean powerUpNotAlive) {
        if (timerFinished && !powerUpIsGone && !powerUpNotAlive) {
            Bitmap powerUpImage = powerUpList.get(index).getPowerUp();
            if (ball.hitPowerUp(mIsPowerUp.getX(), mIsPowerUp.getY())) {
                    if (powerUpImage.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.power_up_0))) {
                        powerUpList.remove(index);
                        powerUpTakenAtLeastOneTime = true;
                        powerUpIsNotAlive = true;
                        numberOfPowerUpsTaken++;
                        score = score + 500;
                        new CountDownTimer(10000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                flipperPowerUpTaken = true;
                            }
                            public void onFinish() {
                                flipperPowerUpTaken = false;
                            }
                        }.start();
                    } else if (powerUpImage.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.power_up_1))) {
                        powerUpList.remove(index);
                        powerUpTakenAtLeastOneTime = true;
                        powerUpIsNotAlive = true;
                        numberOfPowerUpsTaken++;
                        ball_1 = new Ball(ball.getX() , ball.getY());
                        ball_2 = new Ball(ball.getX() , ball.getY());
                        score = score + 200;
                        ballPowerUpTimer = new CountDownTimer(10000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                ballPowerUpTaken = true;
                            }
                            public void onFinish() {
                                ballPowerUpTaken = false;
                            }
                        }.start();
                    } else {
                        powerUpList.remove(index);
                        powerUpTakenAtLeastOneTime = true;
                        powerUpIsNotAlive = true;
                        numberOfPowerUpsTaken++;
                        score = score - 300;
                        new CountDownTimer(10000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                flipperPowerDownTaken = true;
                            }
                            public void onFinish() {
                                flipperPowerDownTaken = false;
                            }
                        }.start();
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
        // "touchSensor" è una variabile collegata alla Switch del Touch presente
        // nelle opzioni. Se la switch è disattivata, utilizza l'accelerometro
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

    // Metodo necessario solamente perchè la classe Game
    // estende la classe View, la quale necessita
    // dell'override del metodo astratto performClick
    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    // Serve a sospendere il gioco in caso di una nuova partita
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Se l'utente non vuole iniziare una nuova partita, allora il touch
        // non ci serve. Quindi ritorno subito.
        if (ignore) {
            return false;
        // Altrimenti se la partita è finita, ed il giocatore non inizia ancora a giocare
        // reimposto semplicemente il livello e aspetto che tocchi lo schermo.
        } else if (gameOver && !start) {
            score = 0;
            lifes = 3;
            resetLevel();
            p = 1;
            gameOver = false;
            return false;
        // In questo caso che l'utente vuole registrare il touch
        } else {
            final int action = event.getAction();
            // Nel caso in cui si rilevano dei movimenti (touch)
            // sull'asse x, prendo questo dato e lo imposto
            // come il punto x del Flipper
            if ((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
                if (!touchSensor) { // Controllo della switch
                    return false;
                }
                float x = event.getX();
                // Se l'utente porta il dito oltre la posizione desiderata
                // allora la impostiamo noi in modo tale che il Flipper non esca
                if (x >= size.x - 202) {
                    event.setLocation(size.x - 200, flipper.getY());
                } else {
                    flipper.setX(x);
                }
            }
            start = true;
            ignore = false;
            return true;
        }
    }

    // Imposta la partita per iniziare
    private void resetLevel() {
        ball.setX(xBall);
        ball.setY(yBall);
        ball.generateSpeed();
        powerUpList = new ArrayList<>();
        brickList = new ArrayList<>();
        generateBricks(context);
        generatePowerUps(context);
        if (ballPowerUpTaken) {
            ballPowerUpTimer.cancel();
            ballPowerUpTaken = false;
            ball_1 = null;
            ball_2 = null;
        }
        ball1NotVisible = false;
        ball2NotVisible = false;
        flipperPowerUpTaken = false;
        flipperPowerDownTaken = false;
        powerUpSkippedAtThisLevel = false;
        powerUpIsNotAlive = false;
        ignore = false;
        timer1Ended = false;
        shouldSkipTimer = false;
        powerUpGone = false;
        seconds = rand.nextInt(25000 - 7000 + 1) + 7000;
    }

    // Check se il giocatore ha vinto o meno
    private void win() {
        // Se l'ArrayList dei powerUp non è vuota, mentre quella
        // dei mattoni lo è, allora l'utente ha skippato il powerUp
        if (!powerUpList.isEmpty() && brickList.isEmpty()) {
            powerUpSkippedAtThisLevel = true;
        }

        // Se l'ArrayList dei mattoni è vuota, attivo il suono relativo,
        // aumento il livello, lo resetto, e imposto la partita per iniziare.
        if (brickList.isEmpty()) {
            StartGame.sound.playWin();
            ++level;
            resetLevel();
            ball.raiseSpeed(level);
            start = false;
        }
    }
}
