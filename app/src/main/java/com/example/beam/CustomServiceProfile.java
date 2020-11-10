package com.example.beam;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

public class CustomServiceProfile {
    public static final UUID SERVICE_UUID = UUID.fromString("83711854-5e89-4224-930a-7adf6e3e4239");
    public static final UUID CHARACTERISTIC_TOKEN_UUID = UUID.fromString("83711855-5e89-4224-930a-7adf6e3e4239");

    public static BluetoothGattCharacteristic CHARACTERISTIC_TOKEN = new BluetoothGattCharacteristic(CHARACTERISTIC_TOKEN_UUID,
            BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ);
}
