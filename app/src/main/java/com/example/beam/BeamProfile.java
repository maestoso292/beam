package com.example.beam;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

/**
 * Class to describe the custom GATT Profile used during BLE connections.
 */
public class BeamProfile {
    /** UUID of custom service */
    public static final UUID SERVICE_UUID = UUID.fromString("83711854-5e89-4224-930a-7adf6e3e4239");
    /** UUID of sole characteristic within custom service */
    public static final UUID CHARACTERISTIC_TOKEN_UUID = UUID.fromString("83711855-5e89-4224-930a-7adf6e3e4239");
    /** UUI of sole characteristic descriptor */
    public static final UUID DESCRIPTOR_TOKEN_UUID = UUID.fromString("83711856-5e89-4224-930a-7adf6e3e4239");

    /**
     * Obtain custom BluetoothGattService of the app using the UUId
     * @return custom BluetoothGattService
     */
    public static BluetoothGattService getBeamService() {
        BluetoothGattService beamService = new BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        beamService.addCharacteristic(getTokenCharacteristic());
        return beamService;
    }

    /**
     * Obtain the sole characteristic within BeamService based on UUID
     * @return BluetoothGattCharacteristic instance
     */
    public static BluetoothGattCharacteristic getTokenCharacteristic() {
        BluetoothGattCharacteristic tokenCharacteristic = new BluetoothGattCharacteristic(CHARACTERISTIC_TOKEN_UUID,
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ);
        tokenCharacteristic.addDescriptor(getTokenDescriptor());
        return tokenCharacteristic;
    }

    /**
     * Obtain descriptor of sole characteristic
     * @return Description of the characteristic and its uses
     */
    public static BluetoothGattDescriptor getTokenDescriptor() {
        BluetoothGattDescriptor tokenDescriptor = new BluetoothGattDescriptor(DESCRIPTOR_TOKEN_UUID,
                BluetoothGattDescriptor.PERMISSION_READ);
        tokenDescriptor.setValue("Token to be passed between devices for attendance confirmation".getBytes());
        return tokenDescriptor;
    }

    /**
     * Concatenate module ID and session ID to form the attendance token.
     * @param moduleId String of the module ID of the session.
     * @param sessionId String of the session ID of the session.
     * @return Attendance token to be passed between BLE devices.
     */
    public static final String createAttendanceToken(String moduleId, String sessionId) {
        return moduleId + " " + sessionId;
    }
}
