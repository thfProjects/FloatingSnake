package com.ayyyyyyylmao.snakeservice;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.libs.shadowlayout.ShadowLayout;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by marko on 3/10/2017.
 */

public class SnakeService extends Service {



    WindowManager windowManager;
    WindowManager.LayoutParams params;
    WindowManager.LayoutParams dialogparams;
    LayoutInflater layoutInflater;
    NotificationManager notificationManager;
    NotificationCompat.Builder notibuilder;
    Notification notification;
    int notificationid;
    RemoteViews notificationview;
    View gameOverDialog;
    int screenwidth;
    int screenheight;
    int cellsize;
    int numcolumns;
    int numrows;
    SnakeCell[][] cells;
    ShadowLayout matrixlayout;
    Snake snake;
    Timer timer;
    int startTime;
    TimerTask timerTask;
    GestureDetector gestureDetector;
    float swipeSensitivity;
    Handler handler;
    Button gameOverYes;
    Button gameOverNo;
    TextView gameOverTitle;
    boolean running;
    int period;
    float progression;
    int highscore;
    TextView highScoretv;
    WindowManager.LayoutParams hsparams;
    View pauseButton;
    WindowManager.LayoutParams pauseButtonParams;
    View pauseMenu;
    boolean paused;
    boolean hidden;
    Button pauseresume;
    Button pauserestart;
    Button pausehide;
    Button pauseSupport;
    Button pausequit;
    SharedPreferences sharedPreferences;
    View highScoreDialog;
    PixelEditText hsEdittext;
    TextView hsTextView;
    Button hsSave;
    Button hsDontSave;
    int newHighScoreIndex;
    View levelChooser;
    WrapContentViewPager wallsViewPager;
    Button levelChooserOk;

    int snakeColor;
    int snakeStartLength = 5;

    int level;

    ArrayList<Integer> scores;
    ArrayList<String> players;

    Snake.OnSnakeEventListener snakeEventListener = new Snake.OnSnakeEventListener() {
        @Override
        public void OnEvent(SnakeEvent se) {
            Random r = new Random();

            LinearLayout.LayoutParams countDownLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            countDownLayoutParams.setMargins(10, 10, 10, 10);

            switch (se.getAction()){
                case SnakeEvent.ACTION_CRASHED:
                    timerTask.cancel();

                    newHighScoreIndex = 10;

                    for(int i = 9;i >= 0 ;i--){
                        if(highscore > scores.get(i)){
                            newHighScoreIndex = i;
                        }
                    }

                    if(newHighScoreIndex < 10){
                        windowManager.addView(highScoreDialog, dialogparams);
                    }else {
                        windowManager.addView(gameOverDialog, dialogparams);
                    }

                    break;
                case SnakeEvent.ACTION_EATEN_POINT:
                    highscore += 100;
                    highScoretv.setText(String.valueOf(highscore));

                    timerTask.cancel();
                    timerTask = new MoveTimerTask();

                    period = (int)(period*progression);
                    addPoint();
                    clearFoodAndMushrooms();
                    addFood();
                    addMushrooms();

                    timer.schedule(timerTask, period, period);

                    break;
                case SnakeEvent.ACTION_EATEN_FOOD:
                    int points = 50*(r.nextInt(3) + 1);
                    highscore += points;
                    highScoretv.setText(String.valueOf(highscore));
                    toast("+ " + points);

                    break;
                case SnakeEvent.ACTION_EATEN_MUSHROOM:
                    int effect = r.nextInt(2);
                    switch (effect){
                        case 0://slow down
                            timerTask.cancel();
                            timerTask = new MoveTimerTask();

                            int slowdownprocenat = 5*(r.nextInt(3) + 1);

                            period = (int)(period*(1 + slowdownprocenat/100f));

                            timer.schedule(timerTask, period, period);

                            toast("slowed down by " + slowdownprocenat + "%");
                            break;
                        case 1://shorten
                            int amount = r.nextInt(snake.getLength()/3) + 1;
                            snake.shorten(amount);

                            toast("- " + amount + " size");
                            break;
                    }
                    break;
            }
        }
    };

    View.OnTouchListener swipeListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }
    };

    View.OnClickListener gameOverDialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == gameOverYes){
                windowManager.removeView(gameOverDialog);
                clearcells();
                chooseLevel();
            }else if(v == gameOverNo){
                windowManager.removeView(gameOverDialog);
                clearcells();
                quit();
            }
        }
    };

    View.OnClickListener pauseButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!paused){
                paused = true;

                timerTask.cancel();

                windowManager.addView(pauseMenu, dialogparams);
            }
        }
    };

    View.OnClickListener pauseMenuListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v==pauseresume){
                windowManager.removeView(pauseMenu);

                timerTask = new MoveTimerTask();
                timer.schedule(timerTask, period, period);

                paused = false;
            }else if(v==pauserestart){
                windowManager.removeView(pauseMenu);

                clearcells();
                chooseLevel();

                paused = false;
            }else if(v==pausehide){
                windowManager.removeView(pauseMenu);
                windowManager.removeView(pauseButton);
                windowManager.removeView(matrixlayout);
                windowManager.removeView(highScoretv);

                hidden = true;
            }else if(v==pausequit){
                windowManager.removeView(pauseMenu);

                clearcells();
                quit();

                paused = false;
            }else if(v==pauseSupport){
                Intent supportIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://twoheadedfetus.com"));
                supportIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(supportIntent);

                windowManager.removeView(pauseMenu);
                windowManager.removeView(pauseButton);
                windowManager.removeView(matrixlayout);
                windowManager.removeView(highScoretv);

                hidden = true;
            }
        }
    };

    View.OnClickListener newHighScoreListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v==hsSave){
                windowManager.removeView(highScoreDialog);

                scores.add(newHighScoreIndex, highscore);
                scores.remove(10);

                players.add(newHighScoreIndex, hsEdittext.getText().toString());
                players.remove(10);

                save();

                hsEdittext.getText().clear();

                windowManager.addView(gameOverDialog, dialogparams);
            }else if(v==hsDontSave){
                windowManager.removeView(highScoreDialog);

                hsEdittext.getText().clear();

                windowManager.addView(gameOverDialog, dialogparams);
            }
        }
    };

    View.OnClickListener levelChooserListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            windowManager.removeView(levelChooser);
            start();
        }
    };

    ViewPager.SimpleOnPageChangeListener wallsListener = new ViewPager.SimpleOnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            level = position;
        }
    };

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.toString().equals("")){
                hsSave.setEnabled(false);
            }else {
                hsSave.setEnabled(true);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        running = false;
        paused = false;
        hidden = false;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        notificationid = 6745;

        timer = new Timer();

        handler = new Handler();

        startTime = 180;
        swipeSensitivity = 250f;
        progression = 0.97f;
        highscore = 0;
        level = 0;

        gestureDetector = new GestureDetector(SnakeService.this, new Swipelistener());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //high scores

        scores = new ArrayList<>();
        players = new ArrayList<>();

        //matrix and cells

        matrixlayout = new ShadowLayout(SnakeService.this);
        matrixlayout.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        matrixlayout.setOnTouchListener(swipeListener);

        screenwidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        screenheight = Resources.getSystem().getDisplayMetrics().heightPixels - getStatusBarHeight();
        //cellsize = (int)getResources().getDimension(R.dimen.cell_size);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = (screenwidth - cellsize*numcolumns)/2;
        params.y = (screenheight - cellsize*numrows)/2;

        //high score dialog

        highScoreDialog = layoutInflater.inflate(R.layout.new_high_score_dialog, null);
        hsEdittext = (PixelEditText) highScoreDialog.findViewById(R.id.playername);
        hsSave = (Button) highScoreDialog.findViewById(R.id.save);
        hsDontSave = (Button) highScoreDialog.findViewById(R.id.dontsave);
        hsTextView = (TextView) highScoreDialog.findViewById(R.id.text);

        hsTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/pixelFJ8pt1__.ttf"));

        hsEdittext.setFocusable(true);
        hsEdittext.addTextChangedListener(textWatcher);

        hsSave.setOnClickListener(newHighScoreListener);
        hsDontSave.setOnClickListener(newHighScoreListener);

        hsSave.setEnabled(false);

        //high Score display

        highScoretv = new TextView(SnakeService.this);
        highScoretv.setBackgroundColor(Color.argb(100, 40, 40, 40));
        highScoretv.setTextColor(Color.WHITE);
        highScoretv.setGravity(Gravity.CENTER);
        highScoretv.setText(String.valueOf(highscore));
        highScoretv.setPadding(5, 5, 5, 5);

        hsparams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        hsparams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        hsparams.y = 40;

        //Pause button and dialog

        pauseButton = layoutInflater.inflate(R.layout.pausebutton, null);
        pauseButton.setOnClickListener(pauseButtonListener);

        pauseButtonParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        pauseButtonParams.gravity = Gravity.BOTTOM | Gravity.LEFT;

        pauseMenu = layoutInflater.inflate(R.layout.pause_menu, null);
        pauseresume = (Button) pauseMenu.findViewById(R.id.resume);
        pauserestart = (Button) pauseMenu.findViewById(R.id.restart);
        pausehide = (Button)pauseMenu.findViewById(R.id.hide);
        pauseSupport = (Button)pauseMenu.findViewById(R.id.support);
        pausequit = (Button) pauseMenu.findViewById(R.id.quit);

        pauseresume.setOnClickListener(pauseMenuListener);
        pauserestart.setOnClickListener(pauseMenuListener);
        pausehide.setOnClickListener(pauseMenuListener);
        pauseSupport.setOnClickListener(pauseMenuListener);
        pausequit.setOnClickListener(pauseMenuListener);

        //game over dialog

        gameOverDialog = layoutInflater.inflate(R.layout.game_over_dialog, null);
        gameOverTitle = (TextView) gameOverDialog.findViewById(R.id.dialogtitle);
        gameOverYes = (Button) gameOverDialog.findViewById(R.id.restartbutton);
        gameOverYes.setOnClickListener(gameOverDialogListener);
        gameOverNo = (Button) gameOverDialog.findViewById(R.id.quitbutton);
        gameOverNo.setOnClickListener(gameOverDialogListener);

        gameOverTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/pixelFJ8pt1__.ttf"));

        int dialogwidth = (int) (screenwidth*0.7f);

        dialogparams = new WindowManager.LayoutParams();
        dialogparams.width = dialogwidth;
        dialogparams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogparams.type = WindowManager.LayoutParams.TYPE_PHONE;
        dialogparams.format = PixelFormat.TRANSLUCENT;
        dialogparams.gravity = Gravity.CENTER;

        //level chooser

        levelChooser = layoutInflater.inflate(R.layout.level_chooser, null);
        wallsViewPager = (WrapContentViewPager) levelChooser.findViewById(R.id.wallsviewpager);
        wallsViewPager.setAdapter(new WallsPagerAdapter(this));
        wallsViewPager.addOnPageChangeListener(wallsListener);
        levelChooserOk = (Button) levelChooser.findViewById(R.id.buttonOk);
        levelChooserOk.setOnClickListener(levelChooserListener);

        //foreground notification

        Intent startIntent = new Intent(SnakeService.this, SnakeService.class);
        startIntent.putExtra("START", true);
        PendingIntent pendingStartIntent = PendingIntent.getService(SnakeService.this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent hsIntent = new Intent(SnakeService.this, HighScoresActivity.class);
        PendingIntent pendingHsIntent = PendingIntent.getActivity(SnakeService.this, 1, hsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent optionsIntent = new Intent(SnakeService.this, SettingsActivity.class);
        PendingIntent pendingOptionsIntent = PendingIntent.getActivity(SnakeService.this, 2, optionsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent exitIntent = new Intent(SnakeService.this, SnakeService.class);
        exitIntent.putExtra("EXIT", true);
        PendingIntent pendingExitIntent = PendingIntent.getService(SnakeService.this, 3, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationview = new RemoteViews(getPackageName(), R.layout.notification_layout);

        notificationview.setOnClickPendingIntent(R.id.play ,pendingStartIntent);
        notificationview.setOnClickPendingIntent(R.id.highscores ,pendingHsIntent);
        notificationview.setOnClickPendingIntent(R.id.options, pendingOptionsIntent);
        notificationview.setOnClickPendingIntent(R.id.exit ,pendingExitIntent);

        notibuilder = new NotificationCompat.Builder(SnakeService.this)
                .setSmallIcon(R.drawable.icon)
                .setCustomContentView(notificationview)
                .setPriority(Notification.PRIORITY_MAX);

        notification = notibuilder.build();

        startForeground(notificationid, notification);

        toast("Snake is ready!");
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        handleintent(intent);

        return START_STICKY;
    }

    public void onDestroy(){
        super.onDestroy();
        if(matrixlayout.isShown()){
            try {
                windowManager.removeView(matrixlayout);
                windowManager.removeView(highScoretv);
                windowManager.removeView(pauseButton);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(gameOverDialog.isShown()){
            try {
                windowManager.removeView(gameOverDialog);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(pauseMenu.isShown()){
            try {
                windowManager.removeView(pauseMenu);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class MoveTimerTask extends TimerTask{

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    snake.move(cells);

                    matrixlayout.invalidate();
                }
            });
        }
    }

    class Swipelistener extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onDown(MotionEvent event){
            super.onDown(event);

            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
            if(Math.abs(velocityX) > swipeSensitivity || Math.abs(velocityY) > swipeSensitivity){
                if(Math.abs(velocityX) > Math.abs(velocityY)){
                    if(velocityX > 0){
                        if(snake.getDirection()!=Snake.LEFT) snake.setDirection(Snake.RIGHT);
                    }else  {
                        if(snake.getDirection()!=Snake.RIGHT) snake.setDirection(Snake.LEFT);
                    }
                }else {
                    if(velocityY > 0){
                        if(snake.getDirection()!=Snake.UP)snake.setDirection(Snake.DOWN);
                    }else {
                        if(snake.getDirection()!=Snake.DOWN) snake.setDirection(Snake.UP);
                    }
                }
                return true;
            }
            return false;
        }

    }

    //GAME METHODS

    private void measure(){
        numcolumns = screenwidth/cellsize;
        numrows = screenheight/cellsize;
    }

    private void createcells(){
        for(int i = 0;i<numrows;i++){
            for(int j = 0;j<numcolumns;j++){
                cells[j][i] = new SnakeCell(SnakeService.this);
                cells[j][i].setCellX(j);
                cells[j][i].setCellY(i);
            }
        }
    }

    private void addcells(){

        LinearLayout parentlayout = new LinearLayout(SnakeService.this);
        parentlayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        parentlayout.setOrientation(LinearLayout.VERTICAL);

        for(int i = 0;i<numrows;i++){

            LinearLayout linearLayout = new LinearLayout(SnakeService.this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            for(int j = 0;j<numcolumns;j++){
                linearLayout.addView(cells[j][i], new LinearLayout.LayoutParams(cellsize, cellsize));
            }
            parentlayout.addView(linearLayout);
        }

        matrixlayout.addView(parentlayout);
    }

    private void clearcells(){
        for(int i = 0;i<numrows;i++){
            for(int j = 0;j<numcolumns;j++){
                cells[j][i].setState(SnakeCell.STATE_NONE);
            }
        }
    }

    private void addPoint(){
        boolean isAdded = false;
        Random r = new Random();
        while(!isAdded){
            int randomX = r.nextInt(numcolumns);
            int randomY = r.nextInt(numrows);
            if(cells[randomX][randomY].getState() == SnakeCell.STATE_NONE){
                cells[randomX][randomY].setState(SnakeCell.STATE_POINT);
                isAdded = true;
            }
        }
    }

    private void addFood(){
        Random r = new Random();
        int i = r.nextInt(3);
        while (i>0){
            int randomX = r.nextInt(numcolumns);
            int randomY = r.nextInt(numrows);
            if(cells[randomX][randomY].getState() == SnakeCell.STATE_NONE){
                cells[randomX][randomY].setState(SnakeCell.STATE_FOOD);
                i--;
            }
        }
    }

    private void addMushrooms(){
        Random r = new Random();
        int i = r.nextInt(4);
        while (i>2){
            int randomX = r.nextInt(numcolumns);
            int randomY = r.nextInt(numrows);
            if(cells[randomX][randomY].getState() == SnakeCell.STATE_NONE){
                cells[randomX][randomY].setState(SnakeCell.STATE_MUSHROOM);
                i--;
            }
        }
    }

    private void clearFoodAndMushrooms(){
        for(int i = 0;i<numrows;i++){
            for(int j = 0;j<numcolumns;j++){
                if(cells[j][i].getState() == SnakeCell.STATE_FOOD || cells[j][i].getState() == SnakeCell.STATE_MUSHROOM){
                    cells[j][i].setState(SnakeCell.STATE_NONE);
                }
            }
        }
    }

    private void addWalls(){
        switch (level){
            case 0:
                break;
            case 1:
                for(int i = 0;i<numrows;i++){
                    if(i==0 || i== numrows-1){
                        for(int j = 0;j<numcolumns;j++){
                            cells[j][i].setState(SnakeCell.STATE_WALL);
                        }
                    }else {
                        cells[0][i].setState(SnakeCell.STATE_WALL);
                        cells[numcolumns-1][i].setState(SnakeCell.STATE_WALL);
                    }
                }
                break;
            case 2:
                int space = 1;
                for(int j = space;j<numcolumns-space;j++){
                    cells[j][numrows/2].setState(SnakeCell.STATE_WALL);
                }
                break;
            case 3:
                int squaresize = 3;

                int offset = 3;

                //draw squares
                for(int numX = offset;numX<numcolumns;numX += numcolumns-(2*offset + squaresize)){
                    for(int numY = offset;numY<numrows;numY += numrows-(2*offset + squaresize)){

                        //draw square
                        for(int i=numY;i<numY+squaresize;i++){
                            for(int j=numX;j<numX+squaresize;j++){
                                cells[j][i].setState(SnakeCell.STATE_WALL);
                            }
                        }
                    }
                }

                //draw cross
                int crosswidth = 4;
                int crossheight = 4;

                for(int j = (numcolumns-(crosswidth - numcolumns%2))/2;j<(numcolumns-(crosswidth - numcolumns%2))/2 + (crosswidth - numcolumns%2);j++){
                    cells[j][numrows/2].setState(SnakeCell.STATE_WALL);
                    if(numrows%2==0){
                        cells[j][numrows/2-1].setState(SnakeCell.STATE_WALL);
                    }
                }

                for(int i = (numrows-(crossheight - numrows%2))/2;i<(numrows-(crossheight - numrows%2))/2 + (crossheight - numrows%2);i++){
                    cells[numcolumns/2][i].setState(SnakeCell.STATE_WALL);
                    if(numcolumns%2==0){
                        cells[numcolumns/2-1][i].setState(SnakeCell.STATE_WALL);
                    }
                }
                break;
            case 4:
                for(int i = 0;i<numcolumns;i++){
                    cells[i][numrows/2].setState(SnakeCell.STATE_WALL);
                    if(numrows%2==0){
                        cells[i][numrows/2-1].setState(SnakeCell.STATE_WALL);
                    }
                }

                for(int j = 0;j<numrows;j++){
                    cells[numcolumns/2][j].setState(SnakeCell.STATE_WALL);
                    if(numcolumns%2==0){
                        cells[numcolumns/2-1][j].setState(SnakeCell.STATE_WALL);
                    }
                }
                break;
            case 5:
                int hOffset = numcolumns/5;
                int vOffset = numrows/5;

                for(int i = hOffset;i<numcolumns-hOffset;i++){
                    if(i == hOffset || i == numcolumns-hOffset-1){
                        for(int j = vOffset;j<numrows-vOffset;j++){
                            if(j < numrows/2-1 || j > numrows - numrows/2){
                                cells[i][j].setState(SnakeCell.STATE_WALL);
                            }
                        }
                    }else if(i < numcolumns/2-1 || i > numcolumns - numcolumns/2){
                        cells[i][vOffset].setState(SnakeCell.STATE_WALL);
                        cells[i][numrows-vOffset-1].setState(SnakeCell.STATE_WALL);
                    }
                }
        }
    }

    private void getScores(){
        scores.clear();
        for(int i = 0;i<10;i++){
            scores.add(sharedPreferences.getInt(level + "score" + (i+1), 0));
        }
    }

    private void getPlayers(){
        players.clear();
        for(int i = 0;i<10;i++){
            players.add(sharedPreferences.getString(level + "player" + (i+1), "player" + (i+1)));
        }
    }

    private void save(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for(int i = 0;i<10;i++){
            editor.putString(level + "player" + (i+1), players.get(i));
            editor.putInt(level + "score" + (i+1), scores.get(i));
        }
        editor.apply();
    }

    private void start(){
        running = true;
        paused = false;
        period = startTime;

        matrixlayout.setShadowed(sharedPreferences.getBoolean("shadowed", true));
        matrixlayout.setShadowOpacity(sharedPreferences.getInt("shadowopacity", 50));

        highscore = 0;
        highScoretv.setText(String.valueOf(highscore));

        getScores();
        getPlayers();

        timerTask = new MoveTimerTask();

        snakeColor = sharedPreferences.getInt("snakecolor", ContextCompat.getColor(SnakeService.this, R.color.snake));
        snake = new Snake(cells, numcolumns-3, numrows-(snakeStartLength+1), numcolumns, numrows, snakeStartLength, snakeColor);
        snake.setOnSnakeEventListener(snakeEventListener);

        addWalls();
        addPoint();
        addFood();
        addMushrooms();

        timer.schedule(timerTask, period, period);
    }

    private void quit(){
        running = false;
        timerTask.cancel();

        windowManager.removeView(matrixlayout);
        windowManager.removeView(highScoretv);
        windowManager.removeView(pauseButton);
    }

    private void chooseLevel(){
        windowManager.addView(levelChooser, dialogparams);
        wallsViewPager.setCurrentItem(level);
        running = true;
    }

    //OTHER

    private void handleintent(Intent intent){
        boolean start = intent.getBooleanExtra("START", false);
        boolean exit = intent.getBooleanExtra("EXIT", false);

        if(start){
            if(!running){

                cellsize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Float.parseFloat(sharedPreferences.getString("cellsize", "18")), getResources().getDisplayMetrics());

                measure();

                cells = new SnakeCell[numcolumns][numrows];

                createcells();
                addcells();

                windowManager.addView(matrixlayout, params);
                windowManager.addView(highScoretv, hsparams);
                windowManager.addView(pauseButton, pauseButtonParams);
                chooseLevel();
            }

            if(hidden){
                windowManager.addView(matrixlayout, params);
                windowManager.addView(highScoretv, hsparams);
                windowManager.addView(pauseButton, pauseButtonParams);
                windowManager.addView(pauseMenu, dialogparams);

                hidden = false;
            }

            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            this.sendBroadcast(it);
        }
        if(exit){

            if(running)timerTask.cancel();

            if(matrixlayout.isShown())windowManager.removeView(matrixlayout);
            if(highScoretv.isShown())windowManager.removeView(highScoretv);
            if(pauseButton.isShown())windowManager.removeView(pauseButton);
            if(highScoreDialog.isShown())windowManager.removeView(highScoreDialog);
            if(pauseMenu.isShown()) windowManager.removeView(pauseMenu);
            if(gameOverDialog.isShown())windowManager.removeView(gameOverDialog);
            if(levelChooser.isShown())windowManager.removeView(levelChooser);
            if(levelChooser.isShown())windowManager.removeView(levelChooser);
            stopSelf();
        }
    }

    private void toast(String text){
        Toast.makeText(SnakeService.this, text, Toast.LENGTH_SHORT).show();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }
}
