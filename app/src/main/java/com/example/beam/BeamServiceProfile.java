package com.example.beam;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.UUID;

// TODO Add what you think is required, don't remove or rename without notifying the group
public class BeamServiceProfile {
    public static final UUID SERVICE_UUID = UUID.fromString("83711854-5e89-4224-930a-7adf6e3e4239");
    // TOKEN is just a label I gave to the characteristic. No particular meaning
    public static final UUID CHARACTERISTIC_TOKEN_UUID = UUID.fromString("83711855-5e89-4224-930a-7adf6e3e4239");

    public static final BluetoothGattCharacteristic CHARACTERISTIC_TOKEN = new BluetoothGattCharacteristic(CHARACTERISTIC_TOKEN_UUID,
            BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ);

    private static String token;
}
