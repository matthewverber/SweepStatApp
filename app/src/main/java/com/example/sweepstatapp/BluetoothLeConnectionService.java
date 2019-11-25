package com.example.sweepstatapp;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.UUID;

public class BluetoothLeConnectionService extends Service {
    private static final String TAG = "BTLeConnectionServ";
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;

    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private boolean mServicesDiscovered = false;
    private boolean mNotificationState = false;

    // Action strings for Intents that will tell this service what to do
    public final static String GATT_START_CONNECTION =
            "com.example.sweepstat.le.GATT_START_CONNECTION";
    public final static String GATT_WRITE_MESSAGE =
            "com.example.sweepstat.le.GATT_WRITE_MESSAGE";
    public final static String GATT_SET_NOTIFICATION =
            "com.example.sweepstat.le.GATT_SET_NOTIFICATION";
    public final static String GATT_STOP_CONNECTION =
            "com.example.sweepstat.le.GATT_STOP_CONNECTION";
    public final static String GATT_QUERY_RUNREADY =
            "com.example.sweepstat.le.GATT_QUERY_RUNREADY";

    // These are Actions that this service will broadcast
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static String RESULT_RUNREADY_STATE =
            "com.example.sweepstat.le.RESULT_RUNREADY_STATE";

    // This is the String message that comes from the Sweepstat. starts with { and ends with }
    public String asdfbuilt_message;
    public StringBuilder built_message = new StringBuilder();

    // these are the relevant UUIDs for the HM-10 module
    public final static UUID CUSTOM_SERVICE =
            UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB");
    public final static UUID CUSTOM_CHARACTERISTIC =
            UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB");
    public final static UUID CUSTOM_CCCD =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    // empty constructor
    public BluetoothLeConnectionService() { }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            onHandleIntent((Intent)msg.obj);
            //stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        HandlerThread thread = new HandlerThread("BLEIntentService");
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service starting...");
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        serviceHandler.sendMessage(msg);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service destroying...");
        serviceLooper.quit();
    }

    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent == null) return;
        if(mBluetoothAdapter == null) {
            // TODO: send broadcast
            Log.e(TAG, "there is no bluetooth adapter");
            return;
        }

        String action = intent.getAction();
        Log.i(TAG, "onHandleIntent: action=" + action);
        Log.d(TAG, "onHandleIntent: mConnectionState="+mConnectionState);
        if(action == null) return;

        // TODO: handle different intent actions: idk.
        switch(action) {
            case GATT_START_CONNECTION:
                String address = intent.getStringExtra("address");
                if(address != null) {
                    boolean result = connect(address);
                    Log.d(TAG, "connect(...) "+ (result?"succeeded":"failed") +" initiation");
                }
                break;
            case GATT_STOP_CONNECTION:
                break;
            case GATT_WRITE_MESSAGE:
                if(mBluetoothGatt != null && mConnectionState == STATE_CONNECTED) {
                    String message = intent.getStringExtra("message");
                    if(message == null) {
                        Log.d(TAG, "message was null");
                        // TODO: send out write failed broadcast
                    } else {
                        boolean result = write(message);
                        Log.d(TAG, "write(...) "+ (result?"succeeded":"failed") +" initiation");
                        // TODO: send out write success/fail broadcast
                    }
                }
                break;
            case GATT_SET_NOTIFICATION:
                if(mBluetoothGatt != null && mConnectionState == STATE_CONNECTED) {
                    boolean enabled = intent.getBooleanExtra("enabled", false);
                    setNotification(enabled);
                }
                break;
            case GATT_QUERY_RUNREADY:
                final Intent outintent = new Intent(RESULT_RUNREADY_STATE);
                outintent.putExtra("runready", (mConnectionState == STATE_CONNECTED && mNotificationState));
                sendBroadcast(outintent);
                break;
        }

    }

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Intent intent = new Intent(intentAction);
                intent.putExtra("address", mBluetoothDeviceAddress);
                sendBroadcast(intent);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery: " + mBluetoothGatt.discoverServices());


            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                mServicesDiscovered = false;
                mNotificationState = false;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "onServicesDiscovered success.");
                for (BluetoothGattService gattService : gatt.getServices()) {
                    Log.v(TAG, "Service UUID Found: " + gattService.getUuid().toString());
                }
                mServicesDiscovered = true;
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered failed with status: " + status);
            }
        }

        @Override
        // idk what this function is for. dont see it getting called.
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            Log.i(TAG, "onCharacteristicRead called");

            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic,
                                          int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "onCharacteristicWrite success!");
            } else {
                Log.i(TAG, "onCharacteristicWrite failed! status: " + status);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "onCharacteristicChanged called");
            // filter by the characteristic we're supposed to listening to
            if(!CUSTOM_CHARACTERISTIC.equals(characteristic.getUuid())) return;

            String value = new String(characteristic.getValue());
            Log.d(TAG, "VALUE GOT: " + value);

            for(int i = 0; i < value.length(); i++) {
                char tmp = value.charAt(i);
                if(tmp == '{') { // start of a new data point
                    built_message.setLength(0);
                }
                if(tmp != ' ') {
                    built_message.append(tmp);
                }
                if(tmp ==  '}') { // end of data point; to broadcast data
                    final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                    intent.putExtra(EXTRA_DATA, built_message.toString());
                    sendBroadcast(intent);
                }

            }
            //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        if (CUSTOM_CHARACTERISTIC.equals(characteristic.getUuid())) {
            byte[] value = characteristic.getValue();
            Log.d(TAG, "VALUE GOT: " + (new String(value)));
//            int flag = characteristic.getProperties();
//            int format = -1;
//            if ((flag & 0x01) != 0) {
//                format = BluetoothGattCharacteristic.FORMAT_UINT16;
//                Log.d(TAG, "Heart rate format UINT16.");
//            } else {
//                format = BluetoothGattCharacteristic.FORMAT_UINT8;
//                Log.d(TAG, "Heart rate format UINT8.");
//            }
//            final int heartRate = characteristic.getIntValue(format, 1);
//            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, new String(value));
        } else {
            Log.d(TAG, "VALUE GOT some hex");
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }

    /**
     * Set the notifications :3
     * prerequisite: mBluetoothGatt != null
     * prerequisite: all services on the device to have been read (IDK IF THIS NEEDS TO BE)
     * @return true if set notification is initiated successfully.
     *         The result is reported asynchronously in
     */
    private void setNotification(boolean enable) {
        if(mBluetoothGatt == null) return;
        // TODO: make this return boolean

        BluetoothGattService mSVC = mBluetoothGatt.getService(CUSTOM_SERVICE);
        BluetoothGattCharacteristic mCH = mSVC.getCharacteristic(CUSTOM_CHARACTERISTIC);
        mBluetoothGatt.setCharacteristicNotification(mCH, enable);

        BluetoothGattDescriptor descriptor = mCH.getDescriptor(CUSTOM_CCCD);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
    }

    /**
     * prerequisite: mBluetoothGatt != null
     * prerequisite: all services on the device to have been read
     * @return true if write is initiated successfully. The result is reported asynchronously in onCharacteristicWrite(...)
     */
    private boolean write(String message) {
        if(mBluetoothGatt == null) return false;

        BluetoothGattService mSVC = mBluetoothGatt.getService(CUSTOM_SERVICE);
        BluetoothGattCharacteristic mCH = mSVC.getCharacteristic(CUSTOM_CHARACTERISTIC);
        mCH.setValue(message);
        return mBluetoothGatt.writeCharacteristic(mCH);
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address.
     * @throws NullPointerException if mBluetoothAdapter is null
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    private boolean connect(final String address) {
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        // Close existing GATT since we want to connect to a new device
        if(mBluetoothGatt != null)
            mBluetoothGatt.close();

        // We want to directly connect to the device, so we are setting the autoConnect parameter to false.
        mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

}
