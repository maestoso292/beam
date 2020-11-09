package com.example.beam;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.content.pm.PackageManager;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Check if the device supports BLE
         */
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}