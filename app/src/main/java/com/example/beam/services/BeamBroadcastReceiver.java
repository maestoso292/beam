package com.example.beam.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Class to handle incoming broadcasts sent my the app. Broadcasts are scheduled on session start
 * and end times.
 */
public class BeamBroadcastReceiver extends BroadcastReceiver {
    /** Constant to ensure that only broadcasts from the app are handled. Value of "beam.intent.action.service"*/
    public static final String INTENT_ACTION = "beam.intent.action.service";
    /** Command request code to start a background service. Value of 0. */
    public static final int START_SERVICE = 0;
    /** Command request code to stop a background service. Value of 1. */
    public static final int STOP_SERVICE = 1;
    // TODO PERIPHERAL_SERVICE may be unnecessary
    /** Service request code to start/stop PeripheralService. Value of 0. */
    public static final int PERIPHERAL_SERVICE = 0;
    /** Service request code to start/stop CentralService. Value of 1. */
    public static final int CENTRAL_SERVICE = 1;
    /** Service request code to start/stop OpenAttendanceService. Value of 2. */
    public static final int OPEN_ATTENDANCE = 2;
    /** Service request code to start/stop CloseAttendanceService. Value of 3. */
    public static final int CLOSE_ATTENDANCE = 3;

    /**
     * Handles an incoming broadcast from only the BEAM app. Starts or stops certain background
     * services based on request codes passed as extras in the intent.
     * @param context Context of the BeamBroadcastReceiver instance.
     * @param intent Intent instance passed from any broadcast transmitter.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Ensures only app broadcasts are handled
        if (!intent.getAction().equals(INTENT_ACTION)) {
            return;
        }
        // Obtain request codes from the intent
        final int SERVICE = intent.getIntExtra("service", -1);
        final int COMMAND = intent.getIntExtra("command", -1);
        // Obtain session details to be passed to the background services
        final String MODULE_ID = intent.getStringExtra("moduleId");
        final String SESSION_ID = intent.getStringExtra("sessionId");

        Intent serviceIntent;

        // Debug statement
        Log.d("MainFragment", "BroadcastReceived");

        // Set service to be started/stopped
        switch (SERVICE) {
            case PERIPHERAL_SERVICE:
                serviceIntent = new Intent(context, PeripheralService.class);
                break;
            case CENTRAL_SERVICE:
                serviceIntent = new Intent(context, CentralService.class);
                break;
            case OPEN_ATTENDANCE:
                serviceIntent = new Intent(context, OpenAttendanceService.class);
                break;
            case CLOSE_ATTENDANCE:
                serviceIntent = new Intent(context, CloseAttendanceService.class);
                break;
            default:
                Toast.makeText(context, "broadcast failed", Toast.LENGTH_SHORT).show();
                return;
        }

        // Pass in session details as extras of the new intent
        serviceIntent.putExtra("moduleId", MODULE_ID);
        serviceIntent.putExtra("sessionId", SESSION_ID);

        // Stop or start the service
        switch (COMMAND) {
            case START_SERVICE:
                context.startService(serviceIntent);
                break;
            case STOP_SERVICE:
                context.stopService(serviceIntent);
                break;
            default:
                Toast.makeText(context, "broadcast failed", Toast.LENGTH_SHORT).show();
        }
    }
}
