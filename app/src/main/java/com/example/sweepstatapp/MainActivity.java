package com.example.sweepstatapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
        //SharedPreferences prefs = this.getSharedPreferences("com.example.sweepstatapp", Context.MODE_PRIVATE);

    }

    /* Method onClick is used for all buttons on the main screen of the app
     * The method first acquires the integer ID of the button pressed
     * The method then checks what button was pressed and runs its code accordingly
     * In the cases of any navigation buttons this will change views accordingly
     * In the case of configurations this will open a new intent displaying options
     */

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences.Editor editor = this.getSharedPreferences("com.example.sweepstatapp", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    public void onClick(View view){

        int id = view.getId();

        if(id == R.id.newExperiment){
            // First, set view to setup -- currently only implementing advanced setup
            Intent setup_advanced = new Intent(this, AdvancedSetup.class);
            startActivity(setup_advanced);
        }

        else if(id == R.id.goHome){
            setContentView(R.layout.activity_main);
        }

        else if(id == R.id.loadConfig){
            // open config loader, can be new intent and return to initial or experiment view
            // not complete for walking skeleton
        }

        else if(id == R.id.ckBluetooth){
            Intent intent = new Intent(this, DeviceListActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.privacyPolicy){
            setContentView(R.layout.privacy_policy);
        }
        else if(id == R.id.credits){
            setContentView(R.layout.credits_page);
        }
        else if(id == R.id.simpleSetup){
            Intent guided_setup = new Intent(this, GuidedSetup.class);
            startActivity(guided_setup);

        }
        else if(id == R.id.simpleSetup){
            Intent guided_setup = new Intent(this, GuidedSetup.class);
            startActivity(guided_setup);

        }
    }
}
