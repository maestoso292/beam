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

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser==null)
        {
            redirectToLoginActivity();
        }
        else{
            /**
             * Determining whether user is student or lecturer
             */
            String email = currentUser.getEmail();
            assert email != null;
            if (email.contains("nottingham.student")) {
                Toast.makeText(this, "Student", Toast.LENGTH_SHORT).show();
            }

            else if (email.contains("nottingham.lecturer")) {
                Toast.makeText(this, "Lecturer", Toast.LENGTH_SHORT).show();
            }

            else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                redirectToLoginActivity();
            }
        }
    }

    private void redirectToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }
}
