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

import com.example.beam.BeamProfile;
import com.example.beam.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CentralService extends Service {
    private static final long SCAN_PERIOD = 15000;

    private boolean serviceStarted;
    private boolean isScanning = false;
    private Handler handler = new Handler();

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCallback gattCallback;
    private ScanCallback scanCallback;

    private ArrayList<BluetoothDevice> deviceList = new ArrayList<>();

    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private String currentSessionId;
    private String tokenReceived;
    private boolean attendanceSuccess;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (serviceStarted) {
            return START_STICKY;
        }
        serviceStarted = true;
        attendanceSuccess = false;

        currentSessionId = intent.getStringExtra("sessionId");
        Toast.makeText(this, "Service started: " + currentSessionId, Toast.LENGTH_SHORT).show();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new NotificationCompat.Builder(this, MainActivity.NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("Central Service")
                    .setContentInfo("Service is running")
                    .setContentIntent(pendingIntent)
                    .setTicker("IDK")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build();
        }
        else {
            notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Central Service")
                    .setContentInfo("Service is running")
                    .setContentIntent(pendingIntent)
                    .setTicker("IDK")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build();
        }
        startForeground(1, notification);

        if (mDatabase == null) {
            Toast.makeText(getApplicationContext(), "No DATABASE", Toast.LENGTH_SHORT).show();
        }
        else {
            mDatabase.child("ble_test").child("central").setValue("Central On");
        }

        /*
        initialiseScanCallback();
        initialiseGattCallback();

        startLeScan();

         */
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        serviceStarted = false;

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (isScanning) {
            isScanning = false;
            stopLeScan();
        }
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
        }
        super.onDestroy();
    }

    private void startLeScan() {
        ScanFilter scanFilter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(BeamProfile.SERVICE_UUID))
                .build();

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                .build();
        // TODO Scanning frequency should follow startLeScan18()
        if (!isScanning) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isScanning) {
                        isScanning = false;
                        stopLeScan();
                    }
                }
            }, SCAN_PERIOD);

            isScanning = true;
            bluetoothAdapter.getBluetoothLeScanner()
                    .startScan(Collections.singletonList(scanFilter), settings, scanCallback);
            Toast.makeText(getApplicationContext(), "Scan started", Toast.LENGTH_SHORT).show();
        }
        else {
            isScanning = false;
            stopLeScan();
        }
    }

    private void stopLeScan() {
        bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        Toast.makeText(getApplicationContext(), "Scan stopped", Toast.LENGTH_SHORT).show();

        findSessionTokenFromDevices();
    }

    private void findSessionTokenFromDevices() {
        try {
            /*
            for (BluetoothDevice device : deviceList) {
                connectToGattServer(device);
                if (tokenReceived != null && tokenReceived.equals(currentSessionId)) {
                    mDatabase.child("ble_test").child("Attendance Token").setValue(currentUser.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                attendanceSuccess = true;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        stopSelf();
                                    }
                                }, 10000);
                            }
                        }
                    });
                }
                else {
                    mDatabase.child("ble_test").child("Failed Token").setValue(currentUser.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                attendanceSuccess = false;
                            }
                        }
                    });
                }
            }
             */
            connectToGattServer(deviceList.get(0));

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (attendanceSuccess) {
                        stopSelf();
                    }
                    else {
                        startLeScan();
                    }
                }
            }, 10000);
        }
        catch (Exception e) {
            mDatabase.child("ble_test").child("exception").setValue(e);
        }
    }

    private void connectToGattServer(BluetoothDevice device) {
        //mDatabase.child("ble_test").child("Central Status").setValue("Connecting to " + device.getName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothGatt = device.connectGatt(CentralService.this, false, gattCallback, BluetoothDevice.TRANSPORT_LE);
        }
        else {
            bluetoothGatt = device.connectGatt(CentralService.this, false, gattCallback);
        }
    }

    private void initialiseScanCallback() {
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                processResult(result);
                Toast.makeText(getApplicationContext(), "Scan success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                for (ScanResult result : results) {
                    processResult(result);
                }
                Toast.makeText(CentralService.this, "Batch Scan success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScanFailed(int errorCode) {
                Toast.makeText(CentralService.this, "Scan failed", Toast.LENGTH_SHORT).show();
            }

            private void processResult(ScanResult result) {
                mDatabase.child("ble_test").child("devices").setValue(result.getDevice().getName());
                if (!deviceList.contains(result.getDevice())) {
                    Toast.makeText(CentralService.this, "Device added: "  + result.getDevice().getName(), Toast.LENGTH_SHORT).show();
                    deviceList.add(result.getDevice());
                }
                if (isScanning) {
                    stopLeScan();
                }
            }
        };
    }

    private void initialiseGattCallback() {
        gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                switch(newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            mDatabase.child("ble_test").child("Central Connected To:").setValue(gatt.getDevice().getName());
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
                    mDatabase.child("ble_test").child("services").push().setValue(service.getUuid().toString());
                    if (BeamProfile.SERVICE_UUID.equals(service.getUuid())) {
                        bluetoothGatt.readCharacteristic(service.getCharacteristic(BeamProfile.CHARACTERISTIC_TOKEN_UUID));
                        return;
                    }
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                mDatabase.child("ble_test").child("ReadResponseReceived").setValue(true);
                mDatabase.child("ble_test").child(characteristic.getUuid().toString()).setValue("VALUE: " + characteristic.getStringValue(0));
                if (BeamProfile.CHARACTERISTIC_TOKEN_UUID.equals(characteristic.getUuid())) {
                    final String stringValue = characteristic.getStringValue(0);
                    tokenReceived = stringValue;
                    if (stringValue.equals(currentSessionId)) {
                        mDatabase.child("ble_test").child("attendance").child(currentUser.getUid()).setValue(true);
                    }
                    else {
                        mDatabase.child("ble_test").child("attendance").child(currentUser.getUid()).setValue(false);
                    }
                }
                else {
                    mDatabase.child("ble_test").child("NO char").setValue("No Char");
                }
            }
        };
    }
}
