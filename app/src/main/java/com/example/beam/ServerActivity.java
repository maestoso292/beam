package com.example.beam;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;

public class ServerActivity extends AppCompatActivity {

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private BluetoothGattServer server;
    private BluetoothGattService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
            finish();
            return;
        }

        server = bluetoothManager.openGattServer(this, callback);

        service = new BluetoothGattService(CustomServiceProfile.SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        service.addCharacteristic(CustomServiceProfile.CHARACTERISTIC_TOKEN);
        service.getCharacteristic(CustomServiceProfile.CHARACTERISTIC_TOKEN_UUID).setValue("HELLO FROM SERVER");
    }

    @Override
    protected void onPause() {
        super.onPause();
        server.close();
    }

    private BluetoothGattServerCallback callback = new BluetoothGattServerCallback() {
        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            byte[] valueRead = server.getService(CustomServiceProfile.SERVICE_UUID).getCharacteristic(CustomServiceProfile.CHARACTERISTIC_TOKEN_UUID).getValue();
            server.sendResponse(device, requestId, GATT_SUCCESS, offset, valueRead);

        }
    };
}