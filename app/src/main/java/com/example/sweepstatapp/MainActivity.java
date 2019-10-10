package com.example.sweepstatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    // Method onClick is used for all buttons in the app
    // The first line acquires the integer ID of the button pressed
    // The method then checks what button was pressed and runs its code accordingly

    public void onClick(View view){
        int id = view.getId();
        if(id == R.id.newExperiment){
            // First, set view to setup -- currently only implementing advanced setup
            setContentView(R.layout.advanced_setup);
        }
        else if(id == R.id.loadConfig){
            // open config loader, can be new intent and return to initial or experiment view
        }
        else if(id == R.id.recentResults){
            // open recent results list -- find out how to save results locally
        }

        // Checks if SweepStat bluetooth device is connected
        // TODO find out what bluetooth device signature sweepstat lem will register as
        else if(id == R.id.ckBluetooth){
            //This if checker should be something along the lines of BluetoothManager.getIsItemConnected(BLUETOOTH.TYPE)
            //In order to do this properly, we need to find out what type of device the LEM will register as
            if(false){
                Toast.makeText(this, "Device is already connected!", Toast.LENGTH_SHORT).show();
            }
            // If LEM is not connected, pressing the button jumps to the device's bluetooth settings
            else{
                Intent openBluetooth = new Intent();
                openBluetooth.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(openBluetooth);
            }
        }
        else if(id == R.id.about){
            // not complete for walking skeleton
        }
        else if(id == R.id.credits){
            // not complete for walking skeleton
        }
        else if(id == R.id.finishAdvanced) {
            // must first check that all fields have been correctly entered
            // then pass parameters preferably to a locally saved params file
            // then must set layout to experiment_runtime

        }
    }
}
