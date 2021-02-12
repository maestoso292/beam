package com.example.beam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.content.pm.PackageManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


// TODO DON'T TOUCH ANYTHING HERE FOR NOW
public class MainActivity extends AppCompatActivity {
    Button buttonPeripheral;
    Button buttonCentral;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    Button logoutButton;

    public static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * For logout button
         */
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        logoutButton = (Button)findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                redirectToLoginActivity();
            }
        });


        /**
         * Check if the device supports BLE
         */
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE NOT SUPPORTED", Toast.LENGTH_SHORT).show();
            finish();
        }

        buttonPeripheral = findViewById(R.id.button_peripheral);
        buttonCentral = findViewById(R.id.button_central);

        buttonPeripheral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), PeripheralActivity.class));
            }
        });

        buttonCentral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), CentralActivity.class));
            }
        });
    }

    /**
     * Redirect user to loginActivity on start
     */
    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser==null)
        {
            redirectToLoginActivity();
        }
    }

    private void redirectToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }
}
