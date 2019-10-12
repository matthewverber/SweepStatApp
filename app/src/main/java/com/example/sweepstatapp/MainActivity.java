package com.example.sweepstatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view){
        if(view.getId() == R.id.newExperiment){
            Intent[] activity = new Intent[1];
            activity[0] = new Intent(MainActivity.this, graph.class);
            startActivities(activity);
            // switch to experiment view; consider implementing viewflipper to walk through each
        }
        else if(view.getId() == R.id.loadConfig){
            // open config loader, can be new intent and return to initial or experiment view
        }
        else if(view.getId() == R.id.recentResults){
            // open recent results list -- find out how to save results locally
        }
        else if(view.getId() == R.id.ckBluetooth){
            // probably toast if correct, else launch intent to settings -> bluetooth
        }
        else if(view.getId() == R.id.about){
            // not ocmplete for walking skeleton
        }
        else if(view.getId() == R.id.credits){
            // not complete for walking skeleton
        }
    }
}
