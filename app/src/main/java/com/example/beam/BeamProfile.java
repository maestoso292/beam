package com.example.beam;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

public class BeamProfile {
    public static final UUID SERVICE_UUID = UUID.fromString("83711854-5e89-4224-930a-7adf6e3e4239");
    public static final UUID CHARACTERISTIC_TOKEN_UUID = UUID.fromString("83711855-5e89-4224-930a-7adf6e3e4239");
    public static final UUID DESCRIPTOR_TOKEN_UUID = UUID.fromString("83711856-5e89-4224-930a-7adf6e3e4239");

    public static BluetoothGattService getBeamService() {
        BluetoothGattService beamService = new BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        beamService.addCharacteristic(getTokenCharacteristic());
        return beamService;
    }

    public static BluetoothGattCharacteristic getTokenCharacteristic() {
        BluetoothGattCharacteristic tokenCharacteristic = new BluetoothGattCharacteristic(CHARACTERISTIC_TOKEN_UUID,
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ);
        tokenCharacteristic.addDescriptor(getTokenDescriptor());
        return tokenCharacteristic;
    }

    public static BluetoothGattDescriptor getTokenDescriptor() {
        BluetoothGattDescriptor tokenDescriptor = new BluetoothGattDescriptor(DESCRIPTOR_TOKEN_UUID,
                BluetoothGattDescriptor.PERMISSION_READ);
        tokenDescriptor.setValue("Token to be passed between devices for attendance confirmation".getBytes());
        return tokenDescriptor;
    }

    public static final String createAttendanceToken(String moduleId, String sessionId) {
        return moduleId + " " + sessionId;
    }
}
