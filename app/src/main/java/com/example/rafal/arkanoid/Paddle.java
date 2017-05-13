package com.example.rafal.arkanoid;

import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rafal on 08.05.17.
 */

public class Paddle implements Parcelable {
    private RectF rect; //tworzenie rectangle dzieki 4 koordynatom

    private int height;
    public int length;
    public int howFarFromLeft; //x
    private int y;
    private int paddleSpeed;


    private Direction paddleDirection = Direction.STOPPED;

    public Paddle(int x, int y){
        height = 40;//25
        length = 200;//150
        paddleSpeed = 1200;
        howFarFromLeft = x/2;
        this.y = y - 15;

        rect = new RectF(this.howFarFromLeft, this.y,
                this.howFarFromLeft + length, this.y + height);



    }

    public RectF getRect(){return rect;}
    public void setDirectionOfPaddle(Direction direction){
        this.paddleDirection = direction;
    }


    public void update(int fps){
        if(paddleDirection.equals(Direction.RIGHT)){
            howFarFromLeft = howFarFromLeft + paddleSpeed / fps;
        }

        if(paddleDirection.equals(Direction.LEFT)){
            howFarFromLeft = howFarFromLeft - paddleSpeed / fps;
        }
        rect.left = howFarFromLeft;
        rect.right = howFarFromLeft + length;
    }


    public Paddle(Parcel parcel){
       // super(parcel);
        height = 40;//25
        length = 200;//150
        paddleSpeed = 1200;
        howFarFromLeft = parcel.readInt();
        this.y = y - 15;

        rect = new RectF(this.howFarFromLeft, this.y,
                this.howFarFromLeft + length, this.y + height);


    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeInt(howFarFromLeft);

       // dest.writeInt(-1);
      //  dest.writeInt(-1);
    }

    // Method to recreate a Question from a Parcel
    public static Creator<Paddle> CREATOR = new Creator<Paddle>() {

        @Override
        public Paddle createFromParcel(Parcel source) {
            return new Paddle(source);
        }

        @Override
        public Paddle[] newArray(int size) {
            return new Paddle[size];
        }

    };
}

