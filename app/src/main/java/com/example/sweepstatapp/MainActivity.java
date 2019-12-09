package com.example.sweepstatapp;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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
            Toast.makeText(getApplicationContext(),"Bluetooth turned on",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }
    }

    // Enter here after user selects "yes" or "no" to enabling radio
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data){
        super.onActivityResult(requestCode, resultCode, Data);
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, Data);
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Do nothing
            }
        }
    }

    private void bluetoothOff(View view){
        mBTAdapter.disable(); // turn off
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
            Intent setup_advanced = new Intent(this, AdvancedSetup.class);
            startActivity(setup_advanced);
        }

        else if(id == R.id.goHome){
            setContentView(R.layout.activity_main);
        }

        else if(id == R.id.loadConfig){
            Intent loadConfiguration = new Intent(this, LoadConfiguration.class);
            startActivity(loadConfiguration);
        }

        else if(id == R.id.recentResults){
            Intent loadData = new Intent(this, LoadData.class);
            startActivity(loadData);
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
    }
}
