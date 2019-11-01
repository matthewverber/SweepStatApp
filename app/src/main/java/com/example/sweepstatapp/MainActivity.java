package com.example.sweepstatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    /* When the app is started, the onCreate method will create the application's splash screen
     * and will default to the main load screen content view. In future versions, this must be
     * updated to include the permission to share data prompt on the first launch, then ignored
     * on subsequent launches. Currently, the app defaults to the main menu, with limited
     * functionality for the purposes of the walking skeleton.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        SharedPreferences prefs = this.getSharedPreferences("com.example.sweepstatapp", Context.MODE_PRIVATE);

    }

    /* Method onClick is used for all buttons on the main screen of the app
     * The method first acquires the integer ID of the button pressed
     * The method then checks what button was pressed and runs its code accordingly
     * In the cases of any navigation buttons this will change views accordingly
     * In the case of configurations this will open a new intent displaying options
     */

    public void onClick(View view){

        int id = view.getId();

        if(id == R.id.newExperiment){
            // First, set view to setup -- currently only implementing advanced setup
            Intent setup_advanced = new Intent(this, AdvancedSetup.class);
            startActivity(setup_advanced);
        }

        else if(id == R.id.loadConfig){
            // open config loader, can be new intent and return to initial or experiment view
            // not complete for walking skeleton
        }

        else if(id == R.id.recentResults){
            // open recent results list -- find out how to save results locally
            // not complete for walking skeleton
        }


        else if(id == R.id.ckBluetooth){
            Intent intent = new Intent(this, DeviceListActivity.class);
            startActivity(intent);
            // This if statement should be something along the lines of BluetoothManager.getIsItemConnected(BLUETOOTH.whateverthesweepstatis)
            // In order to do this properly, we need to find out what type of device the LEM will register as
//            if(false){
//                Toast.makeText(this, "Device is already connected!", Toast.LENGTH_SHORT).show();
//            }
//            // If LEM is not connected, pressing the button jumps to the device's bluetooth settings
//            else{
//                Intent openBluetooth = new Intent();
//                openBluetooth.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
//                startActivity(openBluetooth);
//            }
        }
        else if(id == R.id.about){
            // not complete for walking skeleton
        }
        else if(id == R.id.credits){
            // not complete for walking skeleton
        }
        else if(id == R.id.simpleSetup){
            Intent guided_setup = new Intent(this, GuidedSetup.class);
            startActivity(guided_setup);

        }
    }
}
