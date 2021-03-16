package com.example.beam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras().get("command").equals("START_SERVICE_PERIPHERAL")) {
            Toast.makeText(context, "Peripheral Started", Toast.LENGTH_SHORT).show();
            context.startService(new Intent(context, PeripheralService.class));
        }
        else if (intent.getExtras().get("command").equals("STOP_SERVICE_PERIPHERAL")) {
            Toast.makeText(context, "Peripheral Stopped", Toast.LENGTH_SHORT).show();
            context.stopService(new Intent(context, PeripheralService.class));
        }
    }
}
