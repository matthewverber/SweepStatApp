package com.example.sweepstatapp;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter mBTAdapter;

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        SharedPreferences prefs = this.getSharedPreferences("com.example.sweepstatapp", Context.MODE_PRIVATE);

        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio
        if (mBTAdapter == null) {
            Toast.makeText(getApplicationContext(),"Bluetooth device not found!",Toast.LENGTH_SHORT).show();
            // TODO: Device doesn't support Bluetooth
            // TODO: else assign action to "RUN" experiment button
        }
    }

    private void bluetoothOn(View view){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            //mBluetoothStatus.setText("Bluetooth enabled");
            Toast.makeText(getApplicationContext(),"Bluetooth turned on",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }
    }

    // Enter here after user selects "yes" or "no" to enabling radio
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data){
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                ///mBluetoothStatus.setText("Enabled");
            }
            //else
                //mBluetoothStatus.setText("Disabled");
        }
    }

    private void bluetoothOff(View view){
        mBTAdapter.disable(); // turn off
        //mBluetoothStatus.setText("Bluetooth disabled");
        Toast.makeText(getApplicationContext(),"Bluetooth turned Off", Toast.LENGTH_SHORT).show();
    }

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
            Intent loadConfiguration = new Intent(this, LoadConfiguration.class);
            startActivity(loadConfiguration);
        }

        else if(id == R.id.recentResults){
            // open recent results list -- find out how to save results locally
            // not complete for walking skeleton
            Intent loadData = new Intent(this, LoadData.class);
            startActivity(loadData);
        }
        else if(view.getId() == R.id.ckBluetooth){
            //Toast.makeText(getApplicationContext(),"check bluetooth", Toast.LENGTH_SHORT).show();
            // probably toast if correct, else launch intent to settings -> bluetooth
            Intent intent = new Intent(this, DeviceListActivity.class);
            startActivity(intent);
        }
        else if(view.getId() == R.id.about){
            // not ocmplete for walking skeleton
        }
        else if(view.getId() == R.id.credits){
            // not complete for walking skeleton
        }
    }
}
