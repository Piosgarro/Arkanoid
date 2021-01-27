package com.example.android.arkanoid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.CountDownTimer;
import androidx.core.content.res.ResourcesCompat;

import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;

public class Game extends View implements SensorEventListener, View.OnTouchListener, Runnable {

    private ArrayList<Ball> ballArrayList;
    private ArrayList<Brick> brickList;
    private ArrayList<PowerUp> powerUpList;

    private Bitmap background;
    private Bitmap flipperBit;
    private Bitmap stretch;

    private CountDownTimer flipperPowerDownTimer;
    private CountDownTimer flipperPowerUpTimer;
    private CountDownTimer laserPowerUpTimer;

    private Flipper flipper;

    private PowerUp mIsPowerUp;

    private VelocityTracker mVelocityTracker = null;

    private final boolean touchSensor;
    private boolean flipperPowerDownTaken;
    private boolean flipperPowerUpTaken;
    private boolean gameOver;
    private boolean ignore;
    private boolean laserPowerUpTaken;
    private boolean powerUpGone;
    private boolean powerUpIsNotAlive;
    private boolean powerUpSkippedAtThisLevel;
    private boolean powerUpTakenAtLeastOneTime;
    private boolean shouldSkipTimer;
    private boolean start;
    private boolean threadAlive = true;
    private boolean timer1Ended;

    private final Context context;

    private final Paint levelText;
    private final Paint life;
    private final Paint paint;
    private final Paint scoreText;
    private final Paint textPaint2;

    private final Random rand;

    private final RectF brickRect = new RectF();
    private final RectF feedbackRect = new RectF();
    private final RectF flipperRect = new RectF();
    private final RectF powerUpRect = new RectF();

    private final Sensor accelerometer;
    private final SensorManager sManager;

    private final SharedPreferences mPrefs;

    private final Thread thread = new Thread();

    private float scale;
    private float xDown;
    private float xFlipperDown;
    private float xSpeed;
    private float xVelocity;
    private float yPowerUp;
    private float ySpeed;

    private final float flipperHeight;
    private float flipperWidth;
    public static float brickHeight;
    public static float brickWidth;

    private int ballsActive = 1;
    private int brickLifes = 1;
    private int count = 1;
    private int deviceHeight = 0;
    private int deviceWidth = 0;
    private int index;
    private int level;
    private int lifes;
    private int numberOfPowerUpsTaken;
    private int p = 1;
    private int score;
    private int ticks = 10;
    private long seconds;
    private final int orientation;

    public Game(Context context, int lifes, int score, int orientation) {
        super(context);
        paint = new Paint();
        life = new Paint();
        levelText = new Paint();
        scoreText = new Paint();
        textPaint2 = new Paint();
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
        this.orientation = orientation;
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
        mPrefs = context.getSharedPreferences("save", 0);
        touchSensor = mPrefs.getBoolean("valueTouch", true);

        getSizeAndScale();
        setBackground(context);

        flipperHeight = 40 * scale;
        flipperWidth = 200 * scale;

        brickHeight = 60 * scale;
        brickWidth = 150 * scale;

        // Secondi random per il contatore dei powerUp
        seconds = rand.nextInt(25000 - 7000 + 1) + 7000;

        resetLevel();

        this.setOnTouchListener(this);

    }

    private void generateFlipper() {
        // Flipper
        flipperBit = BitmapFactory.decodeResource(getResources(), R.drawable.flipper);
        flipper = new Flipper((float) (deviceWidth / 2) - (flipperWidth/2), (deviceHeight - 300 * scale));
    }

    // Riempi la lista "ballsArrayList" con 3 palline
    private void generateBalls() {
       for (int i = 0; i < 3; i++) {
            if (i == 0) {
                ballArrayList.add(new Ball((float) deviceWidth/2, flipper.getY()-50, 20 * scale, true));
            } else {
                ballArrayList.add(new Ball((float) deviceWidth/2, flipper.getY()-50, 20 * scale, false));
            }
        }
    }

    // Riempi la lista "powerUpList" con dei powerup
    private void generatePowerUps() {
        int numberOfPowerUps = 4; // Numero dei PowerUp

        powerUpList.clear(); // Svuotiamo l'eventuale ArrayList

        // Se ho preso il powerUp almeno una volta oppure se il powerUp è stato skippato
        // nel livello seguente, allora rigenero il powerUp in una posizione random (nuovamente)
        // e lo aggiungo all'ArrayList, il quale mi servirà dopo per poter "disegnare" i powerUp sullo schermo
        // ------------------------------------------------------------------------------------
        // Imposto questa "if" per evitare che un powerUp venga aggiunto nella stessa posizione di un
        // powerUp già presente.
        float xPowerUp;
        int min = 80;
        int max = 750;
        int s = 0;

        if (powerUpTakenAtLeastOneTime && numberOfPowerUpsTaken >= 1 || (powerUpSkippedAtThisLevel))  {
            for (int i = 0; i < numberOfPowerUps; i++) {
                xPowerUp = (int) (Math.random() * deviceWidth-80);
                while (s == 0) {
                    yPowerUp = rand.nextInt(max - min + 1) + min;
                    if (yPowerUp < 221 || yPowerUp > 690) {
                        s = 1;
                    }
                }
                powerUpList.add(new PowerUp(context, xPowerUp, yPowerUp));
            }
        } else {
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
        int maxRange = 2;
        int minRange = 0;
        int range = maxRange - minRange + 1;
        index = (int)(Math.random() * range) + minRange;
        Log.d("D:","PowerUp Random Index: " + (index));
        mIsPowerUp = powerUpList.get(index);
    }

    // Riempi la lista "brickList" con dei mattoni
    private void generateBricks() {

        if (level == 5 * count) {
            brickLifes++;
            count++;
        }

        int hue = rand.nextInt(9);
        int palette = rand.nextInt(3) + 2;

        float columnSize = (float) deviceWidth / 5;

        for (int i = 3; i < 7; i++) {
            for (int j = 0; j < 5; j++) {
                int skin = (hue + rand.nextInt(palette)) % 9;
                brickList.add(new Brick(context, j*columnSize+(columnSize-brickWidth)/2, i * 100 * scale, brickLifes, skin));
            }
        }
    }

    private void getSizeAndScale() {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        // Controlliamo l'SDK. Se è < 30 (R), allora prendo le dimensioni dello schermo
        // attraverso le vecchie funzioni integrate prima dell'API 30.
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {

            Point size = new Point();
            Display display = windowManager.getDefaultDisplay();
            display.getSize(size); // Prendi le dimensione dello schermo
            deviceWidth = size.x;
            deviceHeight = size.y;

        } else { // Altrimenti utilizzo le funzioni dell'API >= 30.

            final WindowMetrics wm = windowManager.getCurrentWindowMetrics();
            Rect deviceScreen = wm.getBounds();

            // Gets all excluding insets
            final WindowInsets windowInsets = wm.getWindowInsets();
            Insets insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars() | WindowInsets.Type.displayCutout());

            int insetsWidth = insets.right + insets.left;
            int insetsHeight = insets.top + insets.bottom;

            // Legacy size that Display.getSize reports
            final Size legacySize = new Size(deviceScreen.width() - insetsWidth,
                    deviceScreen.height() - insetsHeight);

            // Final device width & height
            deviceWidth = legacySize.getWidth();
            deviceHeight = legacySize.getHeight();
        }

            if (deviceWidth < deviceHeight) {
                scale = (float) deviceWidth / 1080;
            } else {
                scale = (float) deviceHeight / 1080;
            }
        }

        // Imposta sfondo & testo
        private void setBackground(Context context) {
        background = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.background));

        // Disegna il testo
        Typeface titiliumFont = ResourcesCompat.getFont(context, R.font.titilium_regular);
        Typeface atariFont = ResourcesCompat.getFont(context, R.font.atari_classic_smooth);

        life.setTextSize(50); // Imposta la dimensione del carattere

        levelText.setColor(WHITE);
        levelText.setTextSize(60);
        levelText.setAntiAlias(true);
        levelText.setSubpixelText(true);
        levelText.setTypeface(Typeface.create(titiliumFont, Typeface.BOLD));

        scoreText.setColor(WHITE);
        scoreText.setTextSize(60);
        scoreText.setAntiAlias(true);
        scoreText.setSubpixelText(true);
        scoreText.setTextAlign(Paint.Align.CENTER);
        scoreText.setTypeface(Typeface.create(atariFont, Typeface.NORMAL));

        textPaint2.setColor(RED);
        textPaint2.setTextSize(80);
        textPaint2.setTextAlign(Paint.Align.CENTER);
        textPaint2.setTypeface(Typeface.create(atariFont, Typeface.ITALIC));

    }

    protected void onDraw(final Canvas canvas) {

        super.onDraw(canvas);

        // Imposta lo sfondo solamente una volta
        if (stretch == null) {
            stretch = Bitmap.createScaledBitmap(background, deviceWidth, deviceHeight, true);
        }
        canvas.drawBitmap(stretch, 0, 0, paint); // Disegna il background sul Canvas

        // Disegna il flipper scegliendo la sua larghezza
        // in base al PowerUp
        if (flipperPowerUpTaken) {
            flipperWidth = 350 * scale;
        }
        else if (flipperPowerDownTaken) {
            flipperWidth = 150 * scale;
        } else {
            flipperWidth = 200 * scale;
        }

        for (int i = 0; i < ballArrayList.size(); i++) {
            Ball b = ballArrayList.get(i);
            if (b.getStatus()) {
                b.draw(canvas, WHITE);
            }
        }

        flipperRect.set(flipper.getX(), flipper.getY(), flipper.getX() + flipperWidth, flipper.getY() + flipperHeight);
        canvas.drawBitmap(flipperBit, null, flipperRect, paint); // Disegna il Flipper sul Canvas

        // Disegna i mattoni sul Canvas, prendendo ogni oggetto dall'ArrayList
        // e assegnandoli un rettangolo dalle dimensioni specificate in generateBricks()
        for (int i = 0; i < brickList.size(); i++) {
            Brick b = brickList.get(i);
            brickRect.set(b.getX(), b.getY(), b.getX() + brickWidth, b.getY() + brickHeight);
            canvas.drawBitmap(b.getBrick(), null, brickRect, paint);
        }

        if (start) {
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
        }

        // Disegna i powerUp sul Canvas, prendendo ogni oggetto dall'ArrayList
        // e assegnandoli un rettangolo dalle dimensioni specificate in generatePowerUps()
        if (timer1Ended && !powerUpGone && !powerUpIsNotAlive) {
            powerUpRect.set(mIsPowerUp.getX(), mIsPowerUp.getY(), mIsPowerUp.getX() + 80, mIsPowerUp.getY() + 80);
            canvas.drawBitmap(mIsPowerUp.getPowerUp(), null, powerUpRect, paint);
        }

        if (ticks == 5 || ticks == 3 || ticks == 1) {
            feedbackRect.set(flipper.getX(), flipper.getY(), flipper.getX() + flipperWidth, flipper.getY() + flipperHeight);
            canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.feedback_powerup), null, feedbackRect, paint);
        }

        //canvas.drawText("" + xVelocity/(1+Math.abs(xVelocity)), ((float) getWidth() / 2), 1000, textPaint2);
        //canvas.drawText("DX: " + ballArrayList.get(0).getDx(), ((float) getWidth() / 2), 1100, textPaint2);
        //canvas.drawText("DY: " + ballArrayList.get(0).getDy(), ((float) getWidth() / 2), 1200, textPaint2);
        canvas.drawText("" + score, ((float) deviceWidth / 2), 1370, scoreText); // Punteggio al centro dello schermo
        canvas.drawText(getContext().getString(R.string.level) + level, 40, 120, levelText); // Scrivi "Livello: / Level: " nella posizione specificata

        // In base a quante vite ho al momento, scrivi 3, 2 o 1 cuore nella posizione specificata
        switch(lifes) {
            case 1:
                canvas.drawText("\uD83D\uDC9B", deviceWidth - 100, 120, life);
                break;
            case 2:
                canvas.drawText("\uD83D\uDC9B", deviceWidth - 100, 120, life);
                canvas.drawText("\uD83D\uDC9B", deviceWidth - 160, 120, life);
                break;
            case 3:
                canvas.drawText("\uD83D\uDC9B", deviceWidth - 100, 120, life);
                canvas.drawText("\uD83D\uDC9B", deviceWidth - 160, 120, life);
                canvas.drawText("\uD83D\uDC9B", deviceWidth - 220, 120, life);
                break;
            default:
                break;
        }

        run();

    }

    public boolean isCollideWithBrick(Ball ball) {
        for (int i = 0; i < brickList.size(); i++) {
            Brick currentBrick = brickList.get(i);

            if (ball.collideWith(currentBrick)) {
                if (currentBrick.loselife(1)) {
                    brickList.remove(i);
                } else {
                    currentBrick.skin();
                }
                return true;
            }
        }
        return false;
    }

    // Controlla lo stato del gioco.
    // Check sulle vite disponibili
    // oppure se il gioco è terminato.
    public void checkLifes() {

        // Caso in cui hai perso
        if (lifes == 1) {
            --lifes;
            StartGame.sound.playLostLife(); // Se il gioco è finito, attiva il suono relativo

            int scoreGame = mPrefs.getInt("Score", 0);

            if (score > scoreGame) {
                // Cambio il valore score con quello più aggiornato
                SharedPreferences.Editor editor = context.getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putInt("Score", score);
                editor.apply();

                // Get Firebase user
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();

                // Get GoogleAcc user
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
                if (account != null && user != null) {
                    String personEmail = account.getEmail(); //Get user email
                    String personName = account.getDisplayName(); //Get user name

                    // Get reference to our Firebase db and write into it
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    rootRef.child("Users").child(user.getUid()).child("Email").setValue(personEmail);
                    rootRef.child("Users").child(user.getUid()).child("Name").setValue(personName);
                    rootRef.child("Users").child(user.getUid()).child("Score").setValue(score);
                }
            }

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
                    intent.putExtra("orientation", orientation);
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
            --lifes;
            StartGame.sound.playLostLife(); // Se l'utente perde una vita, attiva il suono relativo
            if (flipperPowerUpTaken) {
                flipperPowerUpTimer.cancel();
                flipperPowerUpTaken = false;
            }
            if (flipperPowerDownTaken) {
                flipperPowerDownTimer.cancel();
                flipperPowerDownTaken = false;
            }
            ballArrayList.get(0).setStatus(true);
            ballArrayList.get(0).setCx((float) deviceWidth/2);
            ballArrayList.get(0).setCy(flipper.getY()-20);
            ballArrayList.get(0).setDx(xSpeed);
            ballArrayList.get(0).setDy(-ySpeed);
            ballsActive = 1;
            start = false; // Inizializzo start a false, poichè l'utente non ha ancora "iniziato" a giocare
        }
    }

    // Ad ogni passaggio, controlla se
    // ci sia stata o una collisione,
    // una vittoria o una sconfitta, etc.
    public void update() {
        if (start) {
            win(); // Controlla se l'utente ha vinto

            for (int i = 0; i < ballArrayList.size(); i++) {
                Ball b = ballArrayList.get(i);
                if (b.getStatus()) {
                    if (b.move(deviceWidth, deviceHeight, flipper, flipperWidth, xVelocity)) {
                        if (i == 0) {
                            xSpeed = b.getDx();
                            ySpeed = b.getDy();
                        }
                        if (isCollideWithBrick(b)) {
                            score += 80;
                        }
                    } else {
                        --ballsActive;
                        b.setStatus(false);
                        if (ballsActive == 0) {
                            checkLifes();
                        }
                    }
                }
            }

            // Controlla se l'utente ha preso un powerUp
            checkPowerUp(timer1Ended, powerUpGone, powerUpIsNotAlive);

            // Se l'utente ha raggiunto 1000/2000/3000/4000/x000 ecc.
            // attivo il suono relativo
            if (score == 1000 * p) {
                StartGame.sound.playScoreSound();
                p++;
            }
        }
    }

    // Il controllo sul powerUp, segue la stessa logica utilizzata per la generazione degli stessi.
    private void checkPowerUp(boolean timerFinished, boolean powerUpIsGone, boolean powerUpNotAlive) {
        if (timerFinished && !powerUpIsGone && !powerUpNotAlive) {
            Bitmap powerUpImage = mIsPowerUp.getPowerUp();
            if (ballArrayList.get(0).collideWith(mIsPowerUp)) {
                    if (powerUpImage.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.power_up_0))) {
                        powerUpList.remove(index);
                        powerUpTakenAtLeastOneTime = true;
                        powerUpIsNotAlive = true;
                        numberOfPowerUpsTaken++;
                        score += 500;
                        flipperPowerUpTimer = new CountDownTimer(10000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                --ticks;
                                flipperPowerUpTaken = true;
                            }
                            public void onFinish() {
                                ticks = 0;
                                flipperPowerUpTaken = false;
                            }
                        }.start();
                    } else if (powerUpImage.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.power_up_1))) {
                        powerUpList.remove(index);
                        powerUpTakenAtLeastOneTime = true;
                        powerUpIsNotAlive = true;
                        numberOfPowerUpsTaken++;

                        for (int i = 1; i < ballArrayList.size(); i++) {
                            Ball b = ballArrayList.get(i);
                            b.setStatus(true);
                            b.setCx(ballArrayList.get(0).getCx());
                            b.setCy(ballArrayList.get(0).getCy());
                            // Valori da 1,xxx a 2,xxx
                            float random = 1 + rand.nextFloat() * (2 - 1);

                            int randomSign;
                            if(rand.nextBoolean())
                                randomSign = -1;
                            else
                                randomSign = 1;

                            float xBonusBall = (ballArrayList.get(0).getDx() + random) * randomSign;
                            float yBonusBall = (ballArrayList.get(0).getDy() + random) * randomSign;
                            b.setDx(xBonusBall);
                            b.setDy(yBonusBall);
                        }

                        score += 200;
                        ballsActive = 3;
                    } else if (powerUpImage.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.power_up_2))) {
                        powerUpList.remove(index);
                        powerUpTakenAtLeastOneTime = true;
                        powerUpIsNotAlive = true;
                        numberOfPowerUpsTaken++;
                        score -= 300;
                        flipperPowerDownTimer = new CountDownTimer(10000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                --ticks;
                                flipperPowerDownTaken = true;
                            }
                            public void onFinish() {
                                ticks = 0;
                                flipperPowerDownTaken = false;
                            }
                        }.start();
                    } else {
                        powerUpList.remove(index);
                        powerUpTakenAtLeastOneTime = true;
                        powerUpIsNotAlive = true;
                        numberOfPowerUpsTaken++;
                        score += 200;
                        laserPowerUpTimer = new CountDownTimer(10000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                --ticks;
                                laserPowerUpTaken = true;
                            }
                            public void onFinish() {
                                ticks = 0;
                                laserPowerUpTaken = false;
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
        if(!touchSensor && !gameOver) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                flipper.setX(flipper.getX() - event.values[0] - event.values[0]);

                if (flipper.getX() + event.values[0] > deviceWidth - 200) {
                    flipper.setX(deviceWidth - 200);
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

        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);

        // Se l'utente non vuole iniziare una nuova partita, allora il touch
        // non ci serve. Quindi ritorno subito.
        if (ignore) {
            return false;
            // Altrimenti se la partita è finita, ed il giocatore non inizia ancora a giocare
            // reimposto semplicemente il livello e aspetto che tocchi lo schermo.
        } else if (gameOver && !start) {
            score = 0;
            lifes = 3;
            p = 1;
            gameOver = false;
            return false;
        } else { // In questo caso l'utente vuole registrare il touch
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    if (mVelocityTracker == null) {
                        // Retrieve a new VelocityTracker object to watch the
                        // velocity of a motion.
                        mVelocityTracker = VelocityTracker.obtain();
                    } else {
                        // Reset the velocity tracker back to its initial state.
                        mVelocityTracker.clear();
                    }
                    mVelocityTracker.addMovement(event);
                    // When you want to determine the velocity, call
                    // computeCurrentVelocity(). Then call getXVelocity()
                    // and getYVelocity() to retrieve the velocity for each pointer ID.
                    mVelocityTracker.computeCurrentVelocity(3);
                    // Log velocity of pixels per second
                    // Best practice to use VelocityTrackerCompat where possible.
                    xVelocity = mVelocityTracker.getXVelocity(pointerId);

                    // Nel caso in cui si rilevano dei movimenti (touch)
                    // sull'asse x, prendo questo dato e lo imposto
                    // come il punto x del Flipper
                    if (!touchSensor) { // Controllo della switch
                        xVelocity = 0;
                        return false;
                    } else {
                        float virtual_right_flipper = (xFlipperDown + (event.getX() - xDown));
                        if (virtual_right_flipper + flipperWidth > deviceWidth) {
                            flipper.setX(deviceWidth - flipperWidth);
                        } else if (virtual_right_flipper < 0) {
                            flipper.setX(0);
                        } else {
                            flipper.setX(virtual_right_flipper);
                        }
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    xDown = event.getX();
                    xFlipperDown = flipper.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    xVelocity = 0;
                    break;
            }

            start = true;
            ignore = false;
            return true;
        }
    }

    // Imposta la partita per iniziare
    private void resetLevel() {
        powerUpList = new ArrayList<>();
        brickList = new ArrayList<>();
        ballArrayList = new ArrayList<>();
        ballsActive = 1;
        generateFlipper();
        generateBricks();
        generatePowerUps();
        generateBalls();
        if (flipperPowerUpTaken) {
            flipperPowerUpTimer.cancel();
            flipperPowerUpTaken = false;
        }
        if (flipperPowerDownTaken) {
            flipperPowerDownTimer.cancel();
            flipperPowerDownTaken = false;
        }
        if (laserPowerUpTaken) {
            laserPowerUpTimer.cancel();
            laserPowerUpTaken = false;
        }
        ballArrayList.get(0).generateSpeed();
        ballArrayList.get(0).setCx((float) deviceWidth/2);
        ballArrayList.get(0).setCy(flipper.getY()-20);
        flipperPowerUpTaken = false;
        flipperPowerDownTaken = false;
        powerUpSkippedAtThisLevel = false;
        powerUpIsNotAlive = false;
        ignore = false;
        timer1Ended = false;
        shouldSkipTimer = false;
        powerUpGone = false;
        ticks = 10;
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
            ballArrayList.get(0).raiseSpeed(level);
            start = false;
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        deviceWidth = w;
        deviceHeight = h;

        if (deviceWidth < deviceHeight) {
            scale = (float) deviceWidth / 1080;
        } else {
            scale = (float) deviceHeight / 1080;
        }

        Log.d("DEBUG", "Scale in onSizeChanged: " + scale);

    }

    @Override
    public void run() {
        try {
            if(threadAlive) {
                update();
                postInvalidateOnAnimation();
                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void interruptGame() {
        threadAlive = false;
        thread.interrupt();
        Log.d("W", "Thread ended!");
    }
}
