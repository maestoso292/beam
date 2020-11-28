package com.example.beam;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


public class PeripheralActivity extends AppCompatActivity {

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private BluetoothGattServer bluetoothGattServer;

    private final BluetoothGattServerCallback bluetoothGattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            byte[] characteristicValue = bluetoothGattServer
                    .getService(BeamProfile.SERVICE_UUID)
                    .getCharacteristic(BeamProfile.CHARACTERISTIC_TOKEN_UUID)
                    .getValue();
            bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristicValue);
        }
    };

    private AdvertiseCallback advertiseCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peripheral);

        Button buttonOpenServer = findViewById(R.id.button_open_server);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            advertiseCallback = new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    super.onStartSuccess(settingsInEffect);
                }

                @Override
                public void onStartFailure(int errorCode) {
                    super.onStartFailure(errorCode);
                    finish();
                }
            };
        }

        buttonOpenServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothGattServer == null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        openGattServer();
                    }
                    else {

                    }
                }
            }
        });

        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openGattServer() {
        if (!bluetoothAdapter.isMultipleAdvertisementSupported()) {
            finish();
            return;
        }

        initialiseServer();
        startAdvertising();

        String value = bluetoothGattServer
                .getService(BeamProfile.SERVICE_UUID)
                .getCharacteristic(BeamProfile.CHARACTERISTIC_TOKEN_UUID)
                .getStringValue(0);
    }

    private void initialiseServer() {
        bluetoothGattServer = bluetoothManager.openGattServer(this, bluetoothGattServerCallback);
        BluetoothGattService bluetoothGattService = BeamProfile.getBeamService();
        bluetoothGattService.getCharacteristic(BeamProfile.CHARACTERISTIC_TOKEN_UUID).setValue("Test String");
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startAdvertising() {
        bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (bluetoothLeAdvertiser == null) {
            return;
        }

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                .build();

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .addServiceUuid(new ParcelUuid(BeamProfile.SERVICE_UUID))
                .build();

        bluetoothLeAdvertiser.startAdvertising(settings, data, advertiseCallback);
        Toast.makeText(this, "Started Advertising", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bluetoothLeAdvertiser != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
        }
        if (bluetoothGattServer != null) {
            bluetoothGattServer.close();
        }
    }
}