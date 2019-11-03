package com.example.sweepstatapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class DeviceListActivity extends AppCompatActivity {
    private static final String TAG = "DeviceListActivity";

    private BluetoothAdapter mBtAdapter;
    private static final int REQUEST_ENABLE_BT = 22;

    private boolean mScanning;
    private Handler mHandler;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;



    private TextView mCurrentlyConnectedTextView;
    private LeDeviceListAdapter mLeDeviceListAdapter;

    // TODO: mPreviouslyConnectedDevicesArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        mHandler = new Handler();


        Toast.makeText(getApplicationContext(), "opening devicelistactivity", Toast.LENGTH_SHORT).show();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            //Toast.makeText(this, R.string.message_no_ble, Toast.LENGTH_SHORT).show();
            // TODO: disable BLE-related features
            // finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = bluetoothManager.getAdapter();

        // Initialize textview for currently_connected
        mCurrentlyConnectedTextView = findViewById(R.id.currently_connected);

        // set the text for current status message
        if (mBtAdapter == null) {
            mCurrentlyConnectedTextView.setText(R.string.message_no_bt);
            return;
        }

        if (!mBtAdapter.isEnabled()) {
            mCurrentlyConnectedTextView.setText(R.string.message_bt_off);
            // Requesting user permission to enable Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            mCurrentlyConnectedTextView.setText(R.string.message_no_device);
        }

        // TODO: button to refresh device discovery

        // Initialize array adapters.
        // Find and set up the ListView for available devices
        ListView availableDevicesListView = findViewById(R.id.available_devices);
        mLeDeviceListAdapter = new LeDeviceListAdapter(this, new ArrayList<BluetoothDevice>());
        availableDevicesListView.setAdapter(mLeDeviceListAdapter);
        //availableDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Get a set of currently paired devices
        // TODO: Get and add the set of previously paired devices
        //Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // Register for broadcasts
        //IntentFilter filter = new IntentFilter();
        //filter.addAction(BluetoothDevice.ACTION_FOUND);
        //filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        // TODO: add intents for all bluetooth status changes, etc
        //registerReceiver(mReceiver, filter);

        // Request permissions
        int PERMISSION_ALL = 1;
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ALL);

        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                // Request granted - bluetooth is turning on...
                // TODO: empty the lists
                mCurrentlyConnectedTextView.setText(R.string.message_no_device);
                scanLeDevice(true);
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
            mBtAdapter.stopLeScan(mLeScanCallback);
        }

        // Unregister broadcast listeners
        // this.unregisterReceiver(mReceiver);
    }

    private void scanLeDevice(final boolean enable) {
        // TODO: check if bluetooth is enabled

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    Log.d(TAG, "Scan stopping 1");
                    mBtAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBtAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            Log.d(TAG, "Scan stopping 2");
            mBtAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    // Device scan callback. API < 21
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(device != null) {
                                //String deviceName = device.getName();
                                //String deviceHardwareAddress = device.getAddress(); // MAC address
                                //Log.d(TAG, "device found: " + deviceName);
                                mLeDeviceListAdapter.addDevice(device);
                                mLeDeviceListAdapter.notifyDataSetChanged();

                                //mAvailableDevicesArrayAdapter.add(deviceName + "\n" + deviceHardwareAddress);
                            } else {
                                Toast.makeText(getApplicationContext(),"device is null",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            };


    public void onClick(View v) {
        try {
            Log.d(TAG, "attempting write");
            //bcs.write("hey".getBytes());
        } catch (Exception e) {
            Log.e(TAG, "failed write");
        }

    }


    /**
     * The on-click listener for all devices in the ListViews
     */
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> arg0, View view, int pos, long id) {
            // Cancel discovery because it's costly and we're about to connect
            scanLeDevice(false);

            // Get the BluetoothDevice object
            BluetoothDevice btdevice = mLeDeviceListAdapter.getDevice(pos);
            //Log.d(TAG, "mac address valid: "+BluetoothAdapter.checkBluetoothAddress(address));

            //BluetoothConnectionService bcs = BluetoothConnectionService.getInstance();
            //BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
            //bcs.startClient(device);
/*
            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();*/
        }
    };

    /*
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
    };*/

    private class LeDeviceListAdapter extends ArrayAdapter<BluetoothDevice> {
        private Context mContext;
        private List<BluetoothDevice> devicesList;

        public LeDeviceListAdapter(Context context, ArrayList<BluetoothDevice> devices) {
            super(context, 0, devices);
            mContext = context;
            devicesList = devices;
        }

        public void addDevice(BluetoothDevice device) {
            if(!devicesList.contains(device)) {
                devicesList.add(device);
            }
        }

        public BluetoothDevice getDevice(int pos) {
            return devicesList.get(pos);
        }

        public void clear() {
            devicesList.clear();
        }

        @Override
        public int getCount() {
            return devicesList.size();
        }

        @Override
        public BluetoothDevice getItem(int pos) {
            return devicesList.get(pos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.device_name,parent,false);
            }

            BluetoothDevice currentDevice = devicesList.get(position);
            TextView info = convertView.findViewById(R.id.device_info);

            String text = currentDevice.getName() + "\n" + currentDevice.getAddress();
            Log.d(TAG, "setting text for: " + currentDevice.getName());
            info.setText(text);

            return convertView;
        }

    }





}
