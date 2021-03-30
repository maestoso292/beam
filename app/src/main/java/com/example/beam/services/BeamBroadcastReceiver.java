package com.example.beam.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BeamBroadcastReceiver extends BroadcastReceiver {
    public static final String INTENT_ACTION = "beam.intent.action.service";

    public static final int PERIPHERAL_SERVICE = 0;
    public static final int CENTRAL_SERVICE = 1;

    public static final int START_SERVICE = 0;
    public static final int STOP_SERVICE = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(INTENT_ACTION)) {
            return;
        }
        final int SERVICE = intent.getIntExtra("service", -1);
        final int COMMAND = intent.getIntExtra("command", -1);
        final String MODULE_ID = intent.getStringExtra("moduleId");
        final String SESSION_ID = intent.getStringExtra("sessionId");

        Intent serviceIntent;

        Log.d("MainFragment", "BroadcastReceived");

        // Set service to be started/stopped
        switch (SERVICE) {
            case PERIPHERAL_SERVICE:
                serviceIntent = new Intent(context, PeripheralService.class);
                break;
            case CENTRAL_SERVICE:
                serviceIntent = new Intent(context, CentralService.class);
                break;
            default:
                Toast.makeText(context, "broadcast failed", Toast.LENGTH_SHORT).show();
                return;
        }
        serviceIntent.putExtra("moduleId", MODULE_ID);
        serviceIntent.putExtra("sessionId", SESSION_ID);

        // Stop or start the service
        switch (COMMAND) {
            case START_SERVICE:
                Toast.makeText(context, "broadcast to start service", Toast.LENGTH_SHORT).show();
                context.startService(serviceIntent);
                break;
            case STOP_SERVICE:
                Toast.makeText(context, "broadcast to stop service", Toast.LENGTH_SHORT).show();
                context.stopService(serviceIntent);
                break;
            default:
                Toast.makeText(context, "broadcast failed", Toast.LENGTH_SHORT).show();
                return;
        }

    }
}
