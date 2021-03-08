package com.example.beam;

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
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CentralService extends Service {
    private static final long SCAN_PERIOD = 10000;

    private boolean isScanning = false;
    private Handler handler = new Handler();

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCallback gattCallback;
    private ScanCallback scanCallback;

    private ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
    private List<BluetoothGattService> services;

    private DatabaseReference mDatabase;

    private boolean started;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        if (started) {
            stopSelf();
            return START_NOT_STICKY;
        }
        started = true;
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new NotificationCompat.Builder(this, "123")
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
            mDatabase.child("ble_test").setValue("HIHI");
        }

        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "No ADAPTER", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "ADAPTER AVAILABLE", Toast.LENGTH_SHORT).show();
        }

        initialiseScanCallback();
        initialiseGattCallback();

        startLeScan();

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        started = false;
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
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void connectToGattServer(BluetoothDevice device) {
        bluetoothGatt = device.connectGatt(CentralService.this, false, gattCallback, BluetoothDevice.TRANSPORT_LE);
        Toast.makeText(this, "Connecting to " + bluetoothGatt.getDevice().getName(), Toast.LENGTH_SHORT).show();
    }

    private void initialiseScanCallback() {
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                if (isScanning) {
                    stopLeScan();
                }
                processResult(result);
                Toast.makeText(getApplicationContext(), "Scan success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                if (isScanning) {
                    stopLeScan();
                }
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
                if (!deviceList.contains(result.getDevice())) {
                    Toast.makeText(CentralService.this, "Device added: "  + result.getDevice().getName(), Toast.LENGTH_SHORT).show();
                    deviceList.add(result.getDevice());
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
                            mDatabase.child("ble_test").child("Peripheral").setValue(gatt.getDevice().getName());
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
                services = gatt.getServices();
                for (BluetoothGattService service : services) {
                    mDatabase.child("ble_test").push().setValue(service.getUuid().toString());
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
                    Toast.makeText(CentralService.this, "Characteristic Value: " + stringValue, Toast.LENGTH_SHORT).show();
                    mDatabase.child("ble_test").child(characteristic.getUuid().toString()).setValue("VALUE: " + stringValue);
                }
                else {
                    mDatabase.child("ble_test").child("NO char").setValue("No Char");
                }
            }
        };
    }
}
