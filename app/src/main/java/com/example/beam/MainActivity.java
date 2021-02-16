package com.example.beam;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.beam.models.BeamUser;
import com.example.beam.models.Session;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BeamViewModel beamViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Setup the toolbar at the top
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

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

        beamViewModel = new ViewModelProvider(this).get(BeamViewModel.class);
        beamViewModel.getUserDetails().observe(this, new Observer<BeamUser>() {
            @Override
            public void onChanged(BeamUser beamUser) {
                beamViewModel.getUserModules().observe(MainActivity.this, new Observer<Map<String, String>>() {
                    @Override
                    public void onChanged(Map<String, String> userModules) {
                    }
                });
                beamViewModel.getUserWeeklyTimetable().observe(MainActivity.this, new Observer<Map<String, Map<String, Map<String, Session>>>>() {
                    @Override
                    public void onChanged(Map<String, Map<String, Map<String, Session>>> userWeeklyTimetable) {
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // TODO Unsure if this is the best way to go about updating the action bar overflow menu
        if (navController.getCurrentDestination().getId() == R.id.login_dest) {
            return false;
        }
        else {
            return super.onPrepareOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
        }
        // Returns destination corresponding to id of item
        return NavigationUI.onNavDestinationSelected(item, Navigation.findNavController(this, R.id.nav_host_fragment));
    }
}