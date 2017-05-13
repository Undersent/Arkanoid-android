package com.example.rafal.arkanoid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


    /**
     * Created by rafal on 08.05.17.
     */

    public class Game extends Activity {
        GameView view;
        public Handler handler;
        public static int isFinger;

        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);

            view = new GameView(this);
            handler = new Handler();
            Intent intent = getIntent();
            isFinger = intent.getIntExtra("key",-1);

            setContentView(view);

        }



        @Override
        protected void onResume() {
            super.onResume();


            view.resume();
        }


        @Override
        protected void onPause() {
            super.onPause();


            view.pause();
        }







    }