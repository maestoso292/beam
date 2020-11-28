package com.example.beam;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CentralActivity extends AppCompatActivity {
    private static final String TAG = CentralActivity.class.getSimpleName();
    // In milliseconds
    private static final long SCAN_PERIOD = 10000;

    private boolean isScanning = false;
    private Handler handler = new Handler();

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothGatt bluetoothGatt;

    private BluetoothAdapter.LeScanCallback leScanCallback;
    private ScanCallback scanCallback;

    private ArrayList<BluetoothDevice> deviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);

        Button buttonScan = findViewById(R.id.button_scan_for_device);
        Button buttonConnect = findViewById(R.id.button_connect_to_server);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBelowAPI21()) {
                    startLeScan18();
                } else {
                    startLeScan21();
                }
            }
        });

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deviceList.size() > 0) {
                    connectToGattServer(deviceList.get(0));
                }
            }
        });

        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (isBelowAPI21()) {
            initialiseScanCallback18();
        } else {
            initialiseScanCallback21();
        }
    }

    private boolean isBelowAPI21() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void startLeScan18() {
        /*
        if (!isScanning) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isScanning = false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            isScanning = true;
            bluetoothAdapter.startLeScan(leScanCallback);
        } else {
            isScanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
         */
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startLeScan21() {
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
                    isScanning = false;
                    stopLeScan21();
                }
            }, SCAN_PERIOD);

            isScanning = true;
            bluetoothAdapter.getBluetoothLeScanner()
                    .startScan(Collections.singletonList(scanFilter), settings, scanCallback);
            Toast.makeText(getApplicationContext(), "Scan21 started", Toast.LENGTH_SHORT).show();
        }
        else {
            isScanning = false;
            stopLeScan21();
        }
        /*
        bluetoothAdapter.getBluetoothLeScanner()
                .startScan(Collections.singletonList(scanFilter), settings, scanCallback);

         */
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopLeScan21() {
        bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        Toast.makeText(getApplicationContext(), "Scan21 stopped", Toast.LENGTH_SHORT).show();
    }

    private void connectToGattServer(BluetoothDevice device) {
        bluetoothGatt = device.connectGatt(this, true, gattCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isBelowAPI21()) {

        }
        else {
            stopLeScan21();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initialiseScanCallback18() {
        leScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initialiseScanCallback21() {
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                Toast.makeText(getApplicationContext(), "Scan21 success", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Scan21 success");
                processResult(result);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                Toast.makeText(getApplicationContext(), "Batch Scan21 success", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Batch Scan21 success");
                for (ScanResult result : results) {
                    processResult(result);
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                Toast.makeText(getApplicationContext(), "Scan21 failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "BLE Scan21 failed");
            }

            private void processResult(ScanResult result) {
                Toast.makeText(getApplicationContext(), "Device added: "  + result.getDevice().getName(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Device added: " + result.getDevice().getName());
                deviceList.add(result.getDevice());
                stopLeScan21();
            }
        };
    }

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "onConnectionStateChange");
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "onServicesDiscovered");

            for (BluetoothGattService service : gatt.getServices()) {
                if (BeamProfile.SERVICE_UUID.equals(service.getUuid())) {
                    gatt.readCharacteristic(service.getCharacteristic(BeamProfile.CHARACTERISTIC_TOKEN_UUID));
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d(TAG, "onCharacteristicRead - UUID: " + characteristic.getUuid().toString());
            if (BeamProfile.CHARACTERISTIC_TOKEN_UUID.equals(characteristic.getUuid())) {
                final String value = characteristic.getStringValue(0);
                Toast.makeText(getApplicationContext(), "Characteristic Value: " + value, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.d(TAG, "onCharacteristicChanged");
            final String value = characteristic.getStringValue(0);
            Toast.makeText(getApplicationContext(), "Characteristic Value: " + value, Toast.LENGTH_SHORT).show();
        }
    };
}