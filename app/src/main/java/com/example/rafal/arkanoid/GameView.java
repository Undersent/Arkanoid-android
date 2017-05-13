package com.example.rafal.arkanoid;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;

/**
 * Created by rafal on 08.05.17.
 */
/**
 * Created by rafal on 08.05.17.
 */
class GameView extends View implements Runnable, SensorEventListener {

    private Thread thread = null;
    private boolean isPaused = true;
    private boolean isPlaying;

    private Paint paint;
    private int sizeX;
    private int sizeY;
    public int fps = 60;//???
    private Paddle paddle;
    private Ball ball;
    private long timeThisFrame;
    Brick[] bricks = new Brick[100];
    int num=0;
    int numBricksInColumn = 10;
    int rows = 4;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    MediaPlayer mPlayer;



    int score=0;
    int lives =5;


    public GameView(Context context){
        super(context);
        mPlayer = MediaPlayer.create(context, R.raw.touch);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // Initialize ourHolder and paint objects

        //surfaceHolder = getHolder();
        paint = new Paint();
        setBackgroundResource(R.drawable.background_game);
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        sizeX = size.x;
        sizeY = size.y;
        ball = new Ball(sizeX, sizeY);
        paddle = new Paddle(sizeX, sizeY);
        paddle.update(fps);
        //canvas.drawRect(paddlePhoto.getRect(), paint);






        createGameState();
        // init();
    }

    public void createGameState(){
        ball.reset(sizeX,sizeY);
        score = 0;
        lives = 3;
        int brickWidth = sizeX/ numBricksInColumn;
        int brickHeight = sizeY/10;

        num = 0;

        for(int column = 0; column < numBricksInColumn; column++){
            for(int row =0; row<rows;row++){
                bricks[num] = new Brick(row,column,brickWidth, brickHeight);
                num++;
            }
        }

        if(lives == 0){
            score = 0;
            lives = 3;
        }

    }


    @Override
    public void run() {
        // Looper.prepare();
        // handler = new Handler();
        // Looper.loop();
        while (isPlaying) {

            long startFrameTime = System.currentTimeMillis();
            if (!isPaused) {
                update();
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new Handler(Looper.getMainLooper()).post(new Runnable(){
                @Override
                public void run(){
                    invalidate();

                }
            });

            timeThisFrame = System.currentTimeMillis() - startFrameTime;

            if (timeThisFrame >= 1) {
                fps = 1000 / (int) timeThisFrame;
            }

        }
    }
    //     });

    //   }

    Bitmap paddlePhoto = BitmapFactory.decodeResource(getContext().getResources(),
            R.drawable.cutmypic);
    Bitmap brickPhoto = BitmapFactory.decodeResource(getContext().getResources(),
            R.drawable.brick);
    Bitmap ballPhoto = BitmapFactory.decodeResource(getContext().getResources(),
            R.drawable.ball);


    //Bitmap brickPhoto = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.brick);
    @Override
    public void onDraw(Canvas canvas){

        // if(surfaceHolder.getSurface().isValid()){
        //canvas = surfaceHolder.lockCanvas();
        //Color of background

        //canvas.drawColor(Color.CYAN);
        paint.setColor(Color.BLUE);

        //paddlePhoto

        canvas.drawBitmap(paddlePhoto,null,paddle.getRect(),paint);
        // canvas.drawRect(paddle.getRect(),paint);
        //ball
        //canvas.drawRect(ball.getRect(),paint);
        canvas.drawBitmap(ballPhoto,null,ball.getRect(),paint);
        //bricks
        paint.setColor(Color.MAGENTA);

        for(int i =0; i<num;i++){
            if(bricks[i].getVisibility()){
                //canvas.drawRect(bricks[i].getRect(),paint);
                canvas.drawBitmap(brickPhoto,null,bricks[i].getRect(),paint);
            }
        }

        paint.setColor(Color.WHITE);
        paint.setTextSize(35);
        canvas.drawText("Score: " + score + "   Lives: " + lives, 10, 50, paint);


        if (score == num * 10) {
            paint.setTextSize(90);
            canvas.drawText("WIN!", 10, sizeY / 2, paint);

        }

        if(lives<=0){

            paint.setTextSize(50);
            canvas.drawText("END",10,sizeY/2,paint);

        }


    }



    public void pause(){
        isPlaying = false;
        try{
            thread.join();
        }catch(InterruptedException e){
            Log.d("error","erroreczek wystąpil");
        }
    }

    public void resume(){
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void update() {
        paddle.update(fps);
        ball.update(fps);

        for(int i = 0; i<num;i++)
        {
            if(bricks[i].getVisibility())
            {
                if(RectF.intersects(bricks[i].getRect(),ball.getRect()))
                {
                    bricks[i].setInvisible();
                    ball.reverseYVelocity();
                    score = score + 10;


                    mPlayer.start();
                }
            }
        }


        //Ball to paddlePhoto
        if(RectF.intersects(paddle.getRect(),ball.getRect()))
        {
            ball.setRandomXVelocity();
            ball.reverseYVelocity();
            ball.clearObstacleY(paddle.getRect().top - 2);

        }

        //Ball with bottom of the screen
        if(ball.getRect().bottom > sizeY)
        {
            ball.reverseYVelocity();
            ball.clearObstacleY(sizeY-2);
            //lose a life
            lives--;

            if(lives == 0)
            {
                isPaused = true;
                createGameState();
            }
            if(score == num*10) {
                isPaused = true;
                createGameState();
            }
        }

        //Ball with top of the screen
        if(ball.getRect().top < 0)
        {
            ball.reverseYVelocity();
            //As ball has a height of 10px
            ball.clearObstacleY(2+ball.ballHeight);
            //ball.clearObstacleY(ball.ballHeight+2);

        }

        //Left and right wall collision

        if(ball.getRect().left < 0)
        {
            Log.d("aa","lewa sciana");
            ball.reverseXVelocity();
            //ClearObstacle works on left portion
            ball.clearObstacleX(2);

        }

        if(ball.getRect().right > sizeX-ball.ballWidth)
        {
            Log.d("aa","prawa sciana");
            ball.reverseXVelocity();
            ball.clearObstacleX(sizeX-(2*ball.ballWidth+2));

        }

        if(paddle.getRect().right > sizeX){
            paddle.howFarFromLeft=sizeX-paddle.length;
        }
        else if(paddle.getRect().left < 0){
            paddle.howFarFromLeft=0;
        }




    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:

                isPaused = false;
                if(Game.isFinger==1) {
                    if (motionEvent.getX() > sizeX / 2) {
                        paddle.setDirectionOfPaddle(Direction.RIGHT);
                    } else {
                        paddle.setDirectionOfPaddle(Direction.LEFT);
                    }
                    break;
                }
            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:
                if(Game.isFinger==1) {
                    paddle.setDirectionOfPaddle(Direction.STOPPED);
                    break;
                }
        }

        return true;
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    float [] history = new float[2];
    @Override
    public void onSensorChanged(SensorEvent event) {
                //System.out.println("hahaha");
               // Log.d("ha","hahaha");
                if(Game.isFinger == 0) {
                    float xChange = history[0] - event.values[0];
                    float yChange = history[1] - event.values[1];

                    history[0] = event.values[0];
                    history[1] = event.values[1];


                    if (xChange > 0.3) { //+
                        paddle.setDirectionOfPaddle(Direction.RIGHT);
                    } else if (xChange < -0.3) { //-
                        paddle.setDirectionOfPaddle(Direction.LEFT);
                    } else if (xChange < 0.1 && xChange > -0.1) {
                        paddle.setDirectionOfPaddle(Direction.STOPPED);
                    }


                    if (yChange > 2) {
                        // direction[1] = "DOWN";
                    } else if (yChange < -2) {
                        // direction[1] = "UP";
                    }
                }
    }



    @Override
    public Parcelable onSaveInstanceState() {
        //begin boilerplate code that allows parent classes to save state
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        //end
        Log.d("ccccc","cccccccc");
        ss.paddleToSave = this.paddle;
        ss.ballToSave = this.ball;


        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        //begin boilerplate code so parent classes can restore state
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());
        //end
        //ss.stateToSave.grid.remove(ss.stateToSave.grid.size()-1);
        System.out.println("czytam");
        this.paddle = ss.paddleToSave;
        this.ball = ss.ballToSave;

    }

    static class SavedState extends BaseSavedState {
        Ball ballToSave;
        Paddle paddleToSave;


        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);

            this.paddleToSave = in.readParcelable(null);
            this.ballToSave = in.readParcelable(null);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(this.paddleToSave,0);
            out.writeParcelable(this.ballToSave,0);
            /*
             ArrayList<ArrayList<Cell>> gridToSave=null;
            for(int i=0;i<stateToSave.grid.size()-1;i++){
                gridToSave.add(stateToSave.grid.get(i));
            }
            stateToSave.grid = gridToSave;
            out.writeParcelable(this.stateToSave,0);
             */
        }

        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}

