package com.example.beam;

import android.Manifest;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCallback gattCallback;
    private ScanCallback scanCallback;

    private ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
    private List<BluetoothGattService> services;

    private RecyclerView recyclerView;
    private CentralRecyclerAdapter recyclerAdapter;
    private TextView deviceNameTextView;
    private TextView deviceServiceTextView;
    private TextView deviceCharUuidTextView;
    private TextView deviceCharValueTextView;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);

        if (!isBelowAPI23()) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.central_recycler);
        recyclerAdapter = new CentralRecyclerAdapter();
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        deviceNameTextView = findViewById(R.id.central_device_name);
        deviceServiceTextView = findViewById(R.id.central_device_service_uuid);
        deviceCharUuidTextView = findViewById(R.id.central_device_char_uuid);
        deviceCharValueTextView = findViewById(R.id.central_device_char_value);

        Button buttonScan = findViewById(R.id.button_scan_for_device);
        Button buttonConnect = findViewById(R.id.button_connect_to_server);
        Button buttonRead = findViewById(R.id.button_read);

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLeScan();
            }
        });

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (deviceList.size() > 0) {
                    connectToGattServer(deviceList.get(0));
                }
                else {
                    Toast.makeText(getApplicationContext(), "No devices connected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (services != null) {
                    bluetoothGatt.readCharacteristic(BeamProfile.getTokenCharacteristic());
                }
            }
        });

        initialiseScanCallback();
        initialiseGattCallback();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            Toast.makeText(this, "Location enabled", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isBelowAPI23() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
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
        bluetoothGatt = device.connectGatt(getApplicationContext(), false, gattCallback, BluetoothDevice.TRANSPORT_LE);
        Toast.makeText(this, "Connecting to " + bluetoothGatt.getDevice().getName(), Toast.LENGTH_SHORT).show();
    }

    private void initialiseScanCallback() {
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                stopLeScan();
                processResult(result);
                Toast.makeText(getApplicationContext(), "Scan success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                stopLeScan();
                for (ScanResult result : results) {
                    processResult(result);
                }
                Toast.makeText(getApplicationContext(), "Batch Scan success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScanFailed(int errorCode) {
                Toast.makeText(getApplicationContext(), "Scan failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "BLE Scan failed");
            }

            private void processResult(ScanResult result) {
                if (!deviceList.contains(result.getDevice())) {
                    Toast.makeText(getApplicationContext(), "Device added: "  + result.getDevice().getName(), Toast.LENGTH_SHORT).show();
                    deviceList.add(result.getDevice());
                    deviceNameTextView.setText(result.getDevice().getName());
                }
            }
        };
    }

    private void initialiseGattCallback() {
        gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                Log.d(TAG, "onConnectionStateChange");
                switch(newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            deviceNameTextView.setText(gatt.getDevice().getName() + ": " + status + " - " + newState);
                            mDatabase.child("ble_test").child("Peripheral").setValue(gatt.getDevice().getName());
                            bluetoothGatt = gatt;
                            bluetoothGatt.discoverServices();
                        }
                        break;
                    case BluetoothProfile.STATE_CONNECTING:
                        break;
                    default:
                        deviceNameTextView.setText("No connection");
                        bluetoothGatt.close();
                        bluetoothGatt = null;
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                services = gatt.getServices();
                recyclerAdapter.setServices(services);
                for (BluetoothGattService service : services) {
                    mDatabase.child("ble_test").push().setValue(service.getUuid().toString());
                    if (BeamProfile.SERVICE_UUID.equals(service.getUuid())) {
                        deviceServiceTextView.setText(service.getUuid().toString());
                        bluetoothGatt.readCharacteristic(service.getCharacteristic(BeamProfile.CHARACTERISTIC_TOKEN_UUID));
                        return;
                    }
                }
                deviceServiceTextView.setText("No matching service");
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                deviceCharUuidTextView.setText(characteristic.getUuid().toString());
                mDatabase.child("ble_test").child("ReadResponseReceived").setValue(true);
                mDatabase.child("ble_test").child(characteristic.getUuid().toString()).setValue("VALUE: " + characteristic.getStringValue(0));
                if (BeamProfile.CHARACTERISTIC_TOKEN_UUID.equals(characteristic.getUuid())) {
                    final String stringValue = characteristic.getStringValue(0);
                    Toast.makeText(getApplicationContext(), "Characteristic Value: " + stringValue, Toast.LENGTH_SHORT).show();
                    deviceCharValueTextView.setText("VALUE: " + characteristic.getValue());
                    mDatabase.child("ble_test").child(characteristic.getUuid().toString()).setValue("VALUE: " + stringValue);
                }
                else {
                    deviceCharValueTextView.setText("Wrong characteristic");
                    mDatabase.child("ble_test").child("NO char").setValue("No Char");
                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLeScan();
    }
}