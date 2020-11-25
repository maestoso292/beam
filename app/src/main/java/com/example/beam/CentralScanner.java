package com.example.beam;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class CentralScanner {

    private static final long SCAN_PERIOD = 30000;
    private CentralActivity ca;
    private Handler Handler;
    private BluetoothAdapter BluetoothAdapter;
    private ScanCallback ScanCallback;
    /**
     * Start BLE Scan
     */
    public void startScan() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) ca.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter = bluetoothManager.getAdapter();
        if (BluetoothAdapter != null) {
            BluetoothLeScanner bluetoothLeScanner = BluetoothAdapter.getBluetoothLeScanner();
            if (bluetoothLeScanner != null) {
                if (ScanCallback == null) {
                    Log.d(CentralActivity.TAG, "Start scanning...");
                    ScanCallback = new SampleScanCallback();
                    bluetoothLeScanner.startScan(ScanFilters(), ScanSettings(), ScanCallback);
                    // Will stop the scanning after a set time.
                    Handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ca.getApplicationContext(), "Stop scanning...", Toast.LENGTH_SHORT).show();
                            stopScan();
                        }
                    }, SCAN_PERIOD);
                    ScanCallback = new SampleScanCallback();
                    bluetoothLeScanner.startScan(ScanFilters(), ScanSettings(), ScanCallback);
                }
                else {
                    Toast.makeText(ca.getApplicationContext(), "Already scanning...", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            else {
                Toast.makeText(ca.getApplicationContext(), "Unknown error occurred.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * End BLE Scan
     */
    public void stopScan() {
        Log.d(CentralActivity.TAG, "Stopping Scan...");
        final BluetoothManager bluetoothManager =
                (BluetoothManager) ca.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter = bluetoothManager.getAdapter();
        if (BluetoothAdapter != null) {
            BluetoothLeScanner bluetoothLeScanner = BluetoothAdapter.getBluetoothLeScanner();
            if (bluetoothLeScanner != null) {
                bluetoothLeScanner.stopScan(ScanCallback);
                ScanCallback = null;
                Handler = null;
                return;
            }
        }
    }

    private class SampleScanCallback extends ScanCallback {
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            addResults(results);
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            addResults(result);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Toast.makeText(ca.getApplicationContext(), "Scan failed with error: " + errorCode, Toast.LENGTH_SHORT).show();
        }

        private void addResults(List<ScanResult> results) {
            if (results != null) {
                for (ScanResult result : results) {
                    addResults(result);
                }
            }
        }

        private void addResults(ScanResult result) {
            if (result != null) {
                BluetoothDevice device = result.getDevice();
                if (device != null) {
                    Log.v(CentralActivity.TAG, device.getName() + " " + device.getAddress());
                    return;
                }
            }
            Log.e(CentralActivity.TAG, "SampleScanCallback error");
        }
    }

    /**
     * Filter the scan to only search for specific UUID
     */
    private List<ScanFilter> ScanFilters() {
        List<ScanFilter> scanFilters = new ArrayList<>();
        ScanFilter scanFilter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(BeamServiceProfile.SERVICE_UUID))
                .build();
        scanFilters.add(scanFilter);
        return scanFilters;
    }

    /**
     * Set the BLE Scan to low power mode to save battery
     */
    private ScanSettings ScanSettings() {
        return new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();
    }
}




