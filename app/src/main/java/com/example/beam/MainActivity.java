package com.example.beam;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.widget.Toast;
import android.widget.Button;
import android.view.View;



// TODO DON'T TOUCH ANYTHING HERE FOR NOW
public class MainActivity extends AppCompatActivity {
    public static final String NOTIFICATION_CHANNEL_ID = "BEAM_CHANNEL_ID";

    private NavController navController;
    private BeamViewModel beamViewModel;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        // Setup navigation component
        NavHostFragment hostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = hostFragment.getNavController();
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                invalidateOptionsMenu();
                // Refresh the overflow menu every time destination changes
                if (Arrays.asList(R.id.login_dest, R.id.main_dest).contains(destination)) {
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        beamViewModel = new ViewModelProvider(this).get(BeamViewModel.class);
        if (currentUser != null) {
            beamViewModel.loadUser();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = "Beam Notification Channel";
        String description = "Notification channel for beam app to notify users of ongoing background services";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

    }
}