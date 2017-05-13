package com.example.rafal.arkanoid;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartWindow extends Activity {

    int isFinger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void shake(View v){
        isFinger=0;
        start();
    }

    public void finger(View v){
        isFinger=1;
        start();
    }

    public void start(){
        Intent myIntent = new Intent(StartWindow.this, Game.class);
        myIntent.putExtra("key", isFinger); //Optional parameters
        StartWindow.this.startActivity(myIntent);
    }
}
