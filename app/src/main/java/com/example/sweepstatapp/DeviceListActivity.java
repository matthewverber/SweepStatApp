package com.example.sweepstatapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class DeviceListActivity extends AppCompatActivity {
    private static final String TAG = "DeviceListActivity";

    private BluetoothAdapter mBtAdapter;
    private static final int REQUEST_ENABLE_BT = 22;


    private TextView mCurrentlyConnectedTextView;
    private ArrayAdapter<String> mAvailableDevicesArrayAdapter;
    // TODO: mPreviouslyConnectedDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        Toast.makeText(getApplicationContext(),"opening devicelistactivity",Toast.LENGTH_SHORT).show();

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Initialize currently connected
        mCurrentlyConnectedTextView = findViewById(R.id.currently_connected);

        // set the text for current status message
        if(mBtAdapter == null) {
            mCurrentlyConnectedTextView.setText(R.string.message_no_bt);
            return;
        }

        if(!mBtAdapter.isEnabled()) {
            mCurrentlyConnectedTextView.setText(R.string.message_bt_off);
            // Requesting user permission to enable Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            mCurrentlyConnectedTextView.setText(R.string.message_no_device);
        }

        // TODO: button to refresh device discovery

        // Initialize array adapters.
        mAvailableDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);
        // Find and set up the ListView for available devices
        ListView availableDevicesListView = findViewById(R.id.available_devices);
        availableDevicesListView.setAdapter(mAvailableDevicesArrayAdapter);
        availableDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Get a set of currently paired devices
        // TODO: Get and add the set of previously paired devices
        //Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // Register for broadcasts
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        // TODO: add intents for all bluetooth status changes, etc
        registerReceiver(mReceiver, filter);

        // Request permissions
        int PERMISSION_ALL  = 1;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ALL );

        doDiscovery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                // Request granted - bluetooth is turning on...
                // TODO: empty the lists?
                mCurrentlyConnectedTextView.setText(R.string.message_no_device);
                doDiscovery();
            }
            if (resultCode == RESULT_CANCELED) {
                // Request denied by user, or an error was encountered while
                // attempting to enable bluetooth
                mCurrentlyConnectedTextView.setText(R.string.message_bt_off);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        Log.d(TAG, "doDiscovery()");

        // TODO: Indicate scanning in the title
        //setProgressBarIndeterminateVisibility(true);
        //setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        //findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    /**
     * The on-click listener for all devices in the ListViews
     */
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            Toast.makeText(getApplicationContext(),address,Toast.LENGTH_SHORT).show();
/*
            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();*/
        }
    };

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive intent: " + action);

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device != null) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    Log.d(TAG, "device found: " + deviceName);
                    mAvailableDevicesArrayAdapter.add(deviceName + "\n" + deviceHardwareAddress);
                } else {
                    Toast.makeText(getApplicationContext(),"device is null",Toast.LENGTH_SHORT).show();
                }

            // When discovery is finished
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // TODO: toast/text if no devices found
                // if (mNewDevicesArrayAdapter.getCount() == 0) {

                Toast.makeText(getApplicationContext(),"Bluetooth discovery finished",Toast.LENGTH_SHORT).show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            }
        }
    };
}
