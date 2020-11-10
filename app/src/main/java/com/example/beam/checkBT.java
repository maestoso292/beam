package com.example.beam;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

/**
 * Check the device Bluetooth's status
 * and enables Bluetooth if it's off
 */
public class checkBT {

    /**
     * Check the status of Bluetooth
      */
    public static boolean statusBT (BluetoothAdapter BluetoothAdapter) {

        return BluetoothAdapter != null && BluetoothAdapter.isEnabled();
    }

    /**
     * Enables Bluetooth if it's off
     * @param activity Main Activity
     */

    public static void enableBT(Activity activity) {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableIntent, MainActivity.REQUEST_ENABLE_BT);
    }
}
