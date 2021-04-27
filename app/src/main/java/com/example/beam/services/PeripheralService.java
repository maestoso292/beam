package com.example.beam.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.beam.BeamProfile;
import com.example.beam.MainActivity;
import com.example.beam.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.charset.StandardCharsets;

public class PeripheralService extends Service {
    /** Maximum time allowed for service to advertise itself as a BLE device. Value of 30000ms */
    private static final long ADVERTISE_PERIOD = 300000;
    /** Notification ID for the background service. */
    private static final int SERVICE_NOTIFICATION_ID = 1;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    /** Instance of BLE connection */
    private BluetoothGattServer bluetoothGattServer;
    /** Callback of what to do on BLE connections */
    private BluetoothGattServerCallback bluetoothGattServerCallback;

    /** BLE advertiser */
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    /** Callback of what to do on successful/failed advertising */
    private AdvertiseCallback advertiseCallback;

    /** Reference to root of Firebase Database */
    private DatabaseReference mDatabase;
    /** Current authentication state */
    private FirebaseUser currentUser;

    /** Boolean to check if the service has already been started */
    private boolean serviceStarted;
    /** Boolean to check if a BLE connection has occurred. */
    private boolean hasConnected;
    /** Handler to post runnables */
    private Handler handler = new Handler();
    /** Attendance token of the current session. Should take on the value of the
     *  concatenation of module ID and session ID.
     */
    private String attendanceToken;

    /**
     * Starts the service. Starts advertising as a BLE capable device. May be called multiple
     * times per instance.
     * @param intent Intent instance passed by class that started the service.
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Do nothing is service has already started
        if (serviceStarted) {
            return START_STICKY;
        }
        serviceStarted = true;

        // Obtain session details and form attendance token
        String moduleId = intent.getStringExtra("moduleId");
        String sessionId = intent.getStringExtra("sessionId");
        attendanceToken = BeamProfile.createAttendanceToken(moduleId, sessionId);

        // Create an Intent instance in case user taps the notification. Causes app to open.
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // Notification for BLE advertising
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new NotificationCompat.Builder(this, MainActivity.NOTIF_CHANNEL_SERVICE_ID)
                    .setContentTitle("Advertising tokens for " + moduleId)
                    .setContentText("Sending out tokens to other devices...")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(false)
                    .build();
        }
        else {
            notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Advertising tokens for " + moduleId)
                    .setContentText("Sending out tokens to other devices...")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(false)
                    .build();
        }
        startForeground(SERVICE_NOTIFICATION_ID, notification);

        if (mDatabase == null) {
            Toast.makeText(getApplicationContext(), "No DATABASE", Toast.LENGTH_SHORT).show();
        }

        openGattServer();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Creates instance of the service. Obtains current authentication state, reference to
     * database root, and Bluetooth related classes.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        serviceStarted = false;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    /**
     * Destroys instance of service. Close any BLE connections
     */
    @Override
    public void onDestroy() {
        stopAdvertising();
        if (bluetoothGattServer != null) {
            bluetoothGattServer.close();
        }
        super.onDestroy();
    }

    /**
     * Initialises GATT server and start advertising as a BLE capable device.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openGattServer() {
        // TODO This might be why some devices fail to start advertising. Differing Bluetooth components?
        if (!bluetoothAdapter.isMultipleAdvertisementSupported()) {
            Toast.makeText(getApplicationContext(), "Multiple advertising not supported", Toast.LENGTH_SHORT).show();
        }

        initialiseServer();
        startAdvertising();
    }

    /**
     * Initialises GATT server with BeamService and characteristics from BeamProfile. Writes
     * attendance token into the characteristic.
     */
    private void initialiseServer() {
        Toast.makeText(this, "Opening server", Toast.LENGTH_SHORT).show();

        initialiseGattServerCallback();
        bluetoothGattServer = bluetoothManager.openGattServer(this, bluetoothGattServerCallback);
        BluetoothGattService bluetoothGattService = BeamProfile.getBeamService();
        bluetoothGattService.getCharacteristic(BeamProfile.CHARACTERISTIC_TOKEN_UUID).setValue(attendanceToken);
        bluetoothGattServer.addService(bluetoothGattService);
    }

    /**
     * Initialises what to do during BLE connections. On connection open, stop advertising. On
     * connection close, start advertising. On request to read characteristic, send attendance token
     * over to requester.
     */
    private void initialiseGattServerCallback() {
        bluetoothGattServerCallback = new BluetoothGattServerCallback() {
            @Override
            public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                super.onConnectionStateChange(device, status, newState);
                switch(newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                    case BluetoothProfile.STATE_CONNECTING:
                        hasConnected = true;
                        stopAdvertising();
                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                    case BluetoothProfile.STATE_DISCONNECTING:
                        startAdvertising();
                        break;
                    default:
                }
            }

            @Override
            public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
                if (characteristic.getUuid().equals(BeamProfile.CHARACTERISTIC_TOKEN_UUID)) {
                    bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getStringValue(offset).getBytes(StandardCharsets.UTF_8));
                }
            }
        };
    }

    /**
     * Initialises advertising settings and start advertising as a BLE device.
     */
    private void startAdvertising() {
        bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (bluetoothLeAdvertiser == null) {
            Toast.makeText(this, "No LE Advertiser", Toast.LENGTH_SHORT).show();
            return;
        }

        // Advertise BeamService UUID as defined in BeamProfile
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                .build();

        AdvertiseData advertiseData = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .addServiceUuid(new ParcelUuid(BeamProfile.SERVICE_UUID))
                .build();

        AdvertiseData scanResponseData = new AdvertiseData.Builder()
                .addServiceUuid(new ParcelUuid(BeamProfile.SERVICE_UUID))
                .setIncludeTxPowerLevel(true)
                .build();

        // If advertising failed, continuously attempts to start advertising.
        if (advertiseCallback == null) {
            advertiseCallback = new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    super.onStartSuccess(settingsInEffect);
                    Toast.makeText(getApplicationContext(), "Started advertising", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStartFailure(int errorCode) {
                    super.onStartFailure(errorCode);
                    Toast.makeText(getApplicationContext(), "Failed to start advertising", Toast.LENGTH_SHORT).show();
                    startAdvertising();
                }
            };
        }

        // Start advertising
        bluetoothLeAdvertiser.startAdvertising(settings, advertiseData, scanResponseData, advertiseCallback);
        // After a certain period of time, if no connections have occurred, stop the service
        Runnable runnableStartAdvertising = new Runnable() {
            @Override
            public void run() {
                if (hasConnected) {
                    hasConnected = false;
                    handler.postDelayed(this, ADVERTISE_PERIOD);
                }
                else {
                    Toast.makeText(PeripheralService.this, "No connection over the past minute", Toast.LENGTH_SHORT).show();
                    stopSelf();
                }
            }
        };
        handler.postDelayed(runnableStartAdvertising,ADVERTISE_PERIOD);
    }

    /**
     * Stop advertising as a BLE device.
     */
    private void stopAdvertising() {
        bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
        Toast.makeText(this, "Stopped Advertising", Toast.LENGTH_SHORT).show();
    }
}
