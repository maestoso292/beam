package com.example.beam.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.beam.MainActivity;
import com.example.beam.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Background service to update status of session in database to "Closed"
 */
public class CloseAttendanceService extends Service {
    /** Debug tag */
    private static final String LOG_TAG = "CloseAttdService";
    /** Notification ID for the background service. */
    private static final int SERVICE_NOTIFICATION_ID = 1;
    /** Reference to root of Firebase Database. Used for updating database */
    DatabaseReference mDatabase;
    /** Current signed in user. Used for updating database */
    FirebaseUser currentUser;

    /**
     * Starts the background service and posts a notification to the user. Updates session status
     * in the database.
     * @param intent Intent instance passed by class that started the service
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Obtain session details
        String moduleId = intent.getStringExtra("moduleId");
        String sessionId = intent.getStringExtra("sessionId");

        // Create Intent in the case the user taps the notification. Causes app to start on tap.
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // Notification for closing attendance
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new NotificationCompat.Builder(this, MainActivity.NOTIF_CHANNEL_SERVICE_ID)
                    .setContentTitle("Closing Attendance for " + moduleId)
                    .setContentText("Updating records in database...")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(false)
                    .build();
        }
        else {
            notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Closing Attendance for " + moduleId)
                    .setContentText("Updating records in database...")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(false)
                    .build();
        }
        // Ensure Android does not stop the background service after some time has passed.
        // Notification is necessary
        startForeground(SERVICE_NOTIFICATION_ID, notification);

        // Obtain current date according to Malaysian time
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        final String date = String.format(Locale.ENGLISH, "%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));

        // Update session status in database
        mDatabase.child("timetable")
                .child(date)
                .child(moduleId)
                .child(sessionId)
                .child("status")
                .setValue("Closed");

        mDatabase.child("module_session")
                .child(moduleId)
                .child(sessionId)
                .child("status")
                .setValue("Closed");

        // After 2s has passed, stop the service. Database updates should be finished by this point
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        }, 2000);

        return START_STICKY;
    }

    /**
     * Creates instance of the service. Obtains current user authentication state and
     * reference to database root.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Destroys instance of service and removes service notification.
     */
    @Override
    public void onDestroy() {
        NotificationManagerCompat.from(this).cancel(SERVICE_NOTIFICATION_ID);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
