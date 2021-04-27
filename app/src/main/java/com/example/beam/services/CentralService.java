package com.example.beam.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.beam.BeamProfile;
import com.example.beam.MainActivity;
import com.example.beam.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Background service for taking attendance. Starts a BLE scan, finds the attendance token in
 * other BLE devices, update the database, and start PeripheralService.
 */
public class CentralService extends Service {
    /** Maximum time allowed for a continuous BLE device scan. Value of 15000ms. */
    private static final long SCAN_PERIOD = 15000;
    /** Idle period between BLE device scans. Value of 10000ms. */
    private static final long IDLE_PERIOD = 10000;

    /** Notification ID for the background service. */
    private static final int SERVICE_NOTIFICATION_ID = 1;
    /** Notification ID for successful attendance taking. */
    private static final int SUCCESS_NOTIFICATION_ID = 2;
    /** Boolean to check if the service has already been started */
    private boolean serviceStarted;
    /** Boolean to check if a BLE device scan is occurring. */
    private boolean isScanning;
    /** Handler to post runnables */
    private Handler handler;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCallback gattCallback;
    private ScanCallback scanCallback;

    private BluetoothDevice serverDevice;
    private ArrayList<BluetoothDevice> deviceBlacklist;

    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private String moduleId;
    private String sessionId;
    private String attendanceToken;
    private Boolean attendanceSuccess;

    private Notification notificationScan;
    private Notification notificationIdle;
    private Notification notificationSuccess;

    /** Runnable to stop a BLE scan */
    private Runnable runnableStopScan;
    /** Runnable to start a BLE scan or start PeripheralService if attendance is successful */
    private Runnable runnableStartScan;

    /**
     * Starts the background service and posts a notification to the user. Initialises scan and Gatt
     * callbacks and starts a BLE device scan. May be called several times per instance.
     * @param intent Intent instance passed by class that started the service
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Do nothing if service was already started
        if (serviceStarted) {
            return START_STICKY;
        }

        serviceStarted = true;
        attendanceSuccess = false;

        // Obtain session details and form the attendance token
        moduleId = intent.getStringExtra("moduleId");
        sessionId = intent.getStringExtra("sessionId");
        attendanceToken = BeamProfile.createAttendanceToken(moduleId, sessionId);

        // Create an Intent instance in case user taps the notification. Causes app to open.
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // Notification for BLE scan initialised
        notificationScan = new NotificationCompat.Builder(this, MainActivity.NOTIF_CHANNEL_SERVICE_ID)
                .setContentTitle("Taking Attendance")
                .setContentText("Scanning for nearby devices...")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
                .build();
        // Notification for period when no scan is occuring
        notificationIdle = new NotificationCompat.Builder(this, MainActivity.NOTIF_CHANNEL_SERVICE_ID)
                .setContentTitle("Taking Attendance")
                .setContentText("Waiting to start scan...")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
                .build();
        // Notification for attendance success
        notificationSuccess = new NotificationCompat.Builder(this, MainActivity.NOTIF_CHANNEL_MISC_ID)
                .setContentTitle("Attendance Taken")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .build();

        // Ensure Android does not stop the background service after some time has passed.
        // Notification is necessary
        startForeground(SERVICE_NOTIFICATION_ID, notificationScan);

        if (mDatabase == null) {
            Toast.makeText(getApplicationContext(), "No DATABASE", Toast.LENGTH_SHORT).show();
        }

        handler = new Handler();

        // Initialise callbacks and start a BLE device scan
        initialiseScanCallback();
        initialiseGattCallback();
        startLeScan();

        return START_STICKY;
    }

    /**
     * Creates an instance of the service. Finds current user, database reference, and Bluetooth related instances.
     * Initialises runnables for starting and stopping scans periodically
     */
    @Override
    public void onCreate() {
        super.onCreate();

        serviceStarted = false;
        isScanning = false;

        deviceBlacklist = new ArrayList<>();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Stop scan if currently scanning
        runnableStopScan = new Runnable() {
            @Override
            public void run() {
                if (isScanning) {
                    stopLeScan();
                }
            }
        };

        // If attendance is successful, start PeripheralService (Advertise attendance tokens)
        // Else, start BLE device scan
        runnableStartScan = new Runnable() {
            @Override
            public void run() {
                if (attendanceSuccess) {
                    NotificationManagerCompat.from(CentralService.this).notify(SUCCESS_NOTIFICATION_ID, notificationSuccess);
                    Intent intent = new Intent(CentralService.this, PeripheralService.class);
                    intent.putExtra("sessionId", sessionId);
                    intent.putExtra("moduleId", moduleId);
                    startService(intent);
                    stopSelf();
                }
                else {
                    if (!isScanning) {
                        startLeScan();
                    }
                }
            }
        };
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Destroys current instance of the service. Stops BLE device scans and closes any connections
     * to BLE devices. Remove service notification (Keep the one for attendance success).
     */
    @Override
    public void onDestroy() {
        if (isScanning) {
            isScanning = false;
            bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        }
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
        }
        handler.removeCallbacksAndMessages(null);
        NotificationManagerCompat.from(this).cancel(SERVICE_NOTIFICATION_ID);
        super.onDestroy();
    }

    /**
     * Starts BLE scans for BLE devices that advertise BeamService UUID as defined in BeamProfile
     */
    private void startLeScan() {
        serverDevice = null;

        // Build scan filters to only filter devices that advertise BeamService UUID
        ScanFilter scanFilter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(BeamProfile.SERVICE_UUID))
                .build();

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                .build();

        // Update notification to show that device is scanning for BLE devices
        NotificationManagerCompat.from(this).notify(SERVICE_NOTIFICATION_ID, notificationScan);
        // Stops scanning after a pre-defined scan period.
        handler.postDelayed(runnableStopScan, SCAN_PERIOD);
        // Start scanning
        isScanning = true;
        bluetoothAdapter.getBluetoothLeScanner()
                .startScan(Collections.singletonList(scanFilter), settings, scanCallback);
        Toast.makeText(this, "Scan started", Toast.LENGTH_SHORT).show();
    }

    /**
     * Stops BLE scans. If any devices are available, connect and try find the attendance token for
     * current session
     */
    private void stopLeScan() {
        isScanning = false;
        NotificationManagerCompat.from(this).notify(SERVICE_NOTIFICATION_ID, notificationIdle);
        //scanThreadHandler.postDelayed(runnableStartScan, IDLE_PERIOD);

        bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        Toast.makeText(this, "Scan stopped", Toast.LENGTH_SHORT).show();
        if (serverDevice != null) {
            findSessionTokenFromDevices(serverDevice);
        }
        handler.postDelayed(runnableStartScan, IDLE_PERIOD);
    }

    /**
     * Closes any open connections. Add the device to the blacklist (so it's not connected
     * to again). Open a connection to the BLE device.
     * @param device BLE device to connect to and find the attendance token from
     */
    private void findSessionTokenFromDevices(BluetoothDevice device) {
        try {
            if (bluetoothGatt != null) {
                bluetoothGatt.close();
                bluetoothGatt = null;
            }
            deviceBlacklist.add(device);
            connectToGattServer(device);
        }
        catch (Exception e) {
            // Debug statement to see if something went wrong in the connection phase
            mDatabase.child("ble_test").child("exception").push().setValue(e);
        }
    }

    /**
     * Connects to the BLE device with gattCallback. Location permission required.
     * @param device BLE device to connect to and find the attendance token from
     */
    private void connectToGattServer(BluetoothDevice device) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothGatt = device.connectGatt(CentralService.this, false, gattCallback, BluetoothDevice.TRANSPORT_LE);
        }
        else {
            bluetoothGatt = device.connectGatt(CentralService.this, false, gattCallback);
        }
    }

    /**
     * Initialises what to do when detecting a BLE device that advertises BeamService UUID. Stops
     * scan and add device to a list.
     */
    private void initialiseScanCallback() {
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                processResult(result);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                for (ScanResult result : results) {
                    processResult(result);
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                Toast.makeText(CentralService.this, "Scan failed", Toast.LENGTH_SHORT).show();
            }

            private void processResult(ScanResult result) {
                if (!deviceBlacklist.contains(result.getDevice())) {
                    serverDevice = result.getDevice();
                    stopLeScan();
                }
            }
        };
    }

    /**
     * Initialises what to do during BLE connections. After successful connection, discover services
     * advertised by BLE device. On discovering services, find characteristic that matches
     * characteristic defined by BeamProfile. On read of characteristic, if value matches attendance
     * token, update database (taking attendance) and set attendanceSuccess to true.
     */
    private void initialiseGattCallback() {
        gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                switch(newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            bluetoothGatt = gatt;
                            bluetoothGatt.discoverServices();
                        }
                        break;
                    case BluetoothProfile.STATE_CONNECTING:
                        break;
                    default:
                        bluetoothGatt.close();
                        bluetoothGatt = null;
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                List<BluetoothGattService> services = gatt.getServices();
                for (BluetoothGattService service : services) {
                    if (BeamProfile.SERVICE_UUID.equals(service.getUuid())) {
                        bluetoothGatt.readCharacteristic(service.getCharacteristic(BeamProfile.CHARACTERISTIC_TOKEN_UUID));
                        return;
                    }
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                if (BeamProfile.CHARACTERISTIC_TOKEN_UUID.equals(characteristic.getUuid())) {
                    final String stringValue = characteristic.getStringValue(0);
                    if (stringValue.equals(attendanceToken)) {
                        mDatabase.child("module_record").child(moduleId).child(sessionId).child(currentUser.getUid()).setValue(true);
                        mDatabase.child("student_record").child(currentUser.getUid()).child(moduleId).child(sessionId).setValue(true);
                        attendanceSuccess = true;
                    }
                }
            }
        };
    }
}
