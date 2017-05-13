package com.example.rafal.arkanoid;

/**
 * Created by rafal on 09.05.17.
 */

import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Random;

public class Ball implements Parcelable
{
    RectF rect;
    float xVelocity;
    float yVelocity;
    float ballWidth = 30;
    float ballHeight = 30;

    public Ball(int screenX, int screenY)
    {
        xVelocity = 400;
        yVelocity = -800;
        rect = new RectF();
    }

    public RectF getRect()
    {
        return rect;
    }

    public void update(long fps)
    {
        //xvelocity can be negative
        rect.left = rect.left + (xVelocity/fps);
        rect.top = rect.top + (yVelocity/fps);
        rect.right = rect.left+ballWidth;
        rect.bottom = rect.top + ballHeight;//Doubtful
    }

    public void reverseXVelocity()
    {
        xVelocity = -xVelocity;
    }

    public void reverseYVelocity()
    {
        yVelocity = -yVelocity;
    }

    //This generates 2 nos if 0 reverse else do nothing
    public void setRandomXVelocity()
    {
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if(answer == 0)
            reverseXVelocity();
    }

    public void clearObstacleX(float x)
    {
        rect.left = x;
        rect.right = x + ballWidth;

    }

    public void clearObstacleY(float y)
    {
        rect.bottom = y;
        rect.top = y - ballWidth;
    }

    public void reset(int x, int y)
    {
        rect.left = x / 2;
        rect.top = y - 20;
        rect.right = x / 2 + ballWidth;
        rect.bottom = y - 20 - ballHeight;
    }

    public Ball(Parcel parcel){
        xVelocity = 400;
        yVelocity = -800;
        rect = new RectF(parcel.readFloat(),parcel.readFloat(),parcel.readFloat(),parcel.readFloat());
//left top right bottom


    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeFloat(rect.left);
        dest.writeFloat(rect.top);
        dest.writeFloat(rect.right);
        dest.writeFloat(rect.bottom);

    }

    // Method to recreate a Question from a Parcel
    public static Creator<Ball> CREATOR = new Creator<Ball>() {

        @Override
        public Ball createFromParcel(Parcel source) {
            return new Ball(source);
        }

        @Override
        public Ball[] newArray(int size) {
            return new Ball[size];
        }

    };
}
