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


// TODO DON'T TOUCH ANYTHING HERE FOR NOW
public class MainActivity extends AppCompatActivity {
    public static final String NOTIF_CHANNEL_SERVICE_ID = "BEAM_SERVICE";
    public static final String NOTIF_CHANNEL_MISC_ID = "BEAM_MISC";


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
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = "Beam Service Notification Channel";
        String description = "Notification channel for beam app to notify users of ongoing background services";

        NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_SERVICE_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        notificationManager.createNotificationChannel(channel);

        name = "Beam Miscellaneous Notification Channel";
        description = "Notification channel for beam app to notify users of miscellaneous actions";
        channel = new NotificationChannel(NOTIF_CHANNEL_MISC_ID, name, importance);
        channel.setDescription(description);
        notificationManager.createNotificationChannel(channel);
    }
}