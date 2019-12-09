package com.example.sweepstatapp;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
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
