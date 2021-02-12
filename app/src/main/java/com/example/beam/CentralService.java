package com.example.beam;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.app.Service;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public class CentralService extends Service {

    private final static String TAG = "CentralService";
    private CentralActivity ca;
    private BluetoothManager BluetoothManager;
    private BluetoothAdapter BluetoothAdapter;
    private String BluetoothDeviceAddress;
    private BluetoothGatt BluetoothGatt;
    private int ConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED = "com.example.beam.CentralService.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.beam.CentralService.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.beam.CentralService.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.example.beam.CentralService.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_UUID = "com.example.beam.CentralService.EXTRA_UUID";
    public final static String EXTRA_DATA = "com.example.beam.CentralService.EXTRA_DATA";

    /**
     * Central's service via Android BLE API
     */
    private final BluetoothGattCallback GattCallback = new BluetoothGattCallback() {
        @Override
        /**
         * Indicates that the central has connected or disconnected with a peripheral
         */
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                ConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" + BluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                ConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        /**
         * Indicates that the central has discovered new services
         */
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        /**
         * Reports the result of a characteristic read operation
         */
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        /**
         * Triggered by a remote characteristic notification
         */
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {

            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        /**
         * Helper method that passes action as an argument
         * @param action Action of Peripheral's BLE Service
         */
        private void broadcastUpdate(final String action) {
            final Intent intent = new Intent(action);
            sendBroadcast(intent);
        }

        /**
         * Helper method that passes action and characteristics as arguments
         * @param action Action of Peripheral's BLE Service
         * @param characteristic Characteristic passed during BLE Service operation
         */
        private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {

            final Intent intent = new Intent(action);

            intent.putExtra(EXTRA_UUID, characteristic.getUuid().toString());

            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();

            if (data != null && data.length > 0) {

                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + HexToString(data));
            } else {
                intent.putExtra(EXTRA_DATA, "0");
            }

            sendBroadcast(intent);
        }

        /**
         * Initializes a reference to the local Bluetooth adapter.
         *
         * @return Return true if the initialization is successful.
         */
        public boolean initialize() {
            // For API level 18 and above, get a reference to BluetoothAdapter through
            // BluetoothManager.
            if (BluetoothManager == null) {
                BluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                if (BluetoothManager == null) {
                    Log.e(TAG, "Unable to initialize BluetoothManager.");
                    return false;
                }
            }
            BluetoothAdapter = BluetoothManager.getAdapter();
            if (BluetoothAdapter == null) {
                Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
                return false;
            }
            return true;
        }

        public boolean connect(final String address) {

            if (BluetoothAdapter == null || address == null) {
                Log.w(ca.TAG, "BluetoothAdapter not initialized or unspecified address.");
                return false;
            }

            // Previously connected device.  Try to reconnect.
            if (BluetoothDeviceAddress != null && address.equals(BluetoothDeviceAddress) && BluetoothGatt != null) {
                Log.d(ca.TAG, "Trying to use an existing mBluetoothGatt for connection.");
                if (BluetoothGatt.connect()) {
                    ConnectionState = STATE_CONNECTING;
                    return true;
                } else {
                    return false;
                }
            }

            BluetoothDevice device = BluetoothAdapter.getRemoteDevice(address);
            if (device == null) {
                Log.w(ca.TAG, "Device not found.  Unable to connect.");
                return false;
            }

            BluetoothGatt = device.connectGatt(CentralService.this, false, GattCallback);
            BluetoothDeviceAddress = address;
            ConnectionState = STATE_CONNECTING;

            Log.d(ca.TAG, "Trying to create a new connection.");

            return true;
        }
        /**
         * After using a given BLE device, the app must call this method to ensure resources are
         * released properly.
         */
        public void close() {
            if (BluetoothGatt == null) {
                return;
            }
            BluetoothGatt.close();
            BluetoothGatt = null;
        }

        /**
         * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
         * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
         * callback.
         *
         * @param characteristic The characteristic to read from.
         */
        public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
            if (BluetoothAdapter == null || BluetoothGatt == null) {
                Log.w(TAG, "BluetoothAdapter not initialized");
                return;
            }
            BluetoothGatt.readCharacteristic(characteristic);
        }

        /**
         * Enables or disables notification on a given characteristic.
         *
         * @param characteristic Characteristic to act on.
         * @param enabled If true, enable notification.  False otherwise.
         */
        public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
            if (BluetoothAdapter == null || BluetoothGatt == null) {
                Log.w(TAG, "BluetoothAdapter not initialized");
                return;
            }
            BluetoothGatt.setCharacteristicNotification(characteristic, enabled);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(getString(R.string.CLIENT_CHARACTERISTIC_CONFIG)));
            if (enabled) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            } else {
                descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            }
            BluetoothGatt.writeDescriptor(descriptor);
        }

        /**
         * Retrieves a list of supported GATT services on the connected device. This should be
         * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
         *
         * @return A {@code List} of supported services.
         */
        public List<BluetoothGattService> getSupportedGattServices() {
            if (BluetoothGatt == null) {
                return null;
            }
            return BluetoothGatt.getServices();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static String HexToString(byte[] data) {
        final StringBuilder sb = new StringBuilder(data.length);

        for(byte byteChar : data) {
            sb.append(String.format("%02X ", byteChar));
        }

        return sb.toString();
    }
}


