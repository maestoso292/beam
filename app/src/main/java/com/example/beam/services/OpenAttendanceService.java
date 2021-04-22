package com.example.beam.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.beam.MainActivity;
import com.example.beam.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class OpenAttendanceService extends Service {
    private static final String LOG_TAG = "OpenAttdService";
    private static final int SERVICE_NOTIFICATION_ID = 1;

    DatabaseReference mDatabase;
    FirebaseUser currentUser;

    String moduleId;
    String sessionId;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        moduleId = intent.getStringExtra("moduleId");
        sessionId = intent.getStringExtra("sessionId");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new NotificationCompat.Builder(this, MainActivity.NOTIF_CHANNEL_SERVICE_ID)
                    .setContentTitle("Opening Attendance for " + moduleId)
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
                    .setContentTitle("Opening Attendance for " + moduleId)
                    .setContentText("Updating records in database...")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(false)
                    .build();
        }
        startForeground(SERVICE_NOTIFICATION_ID, notification);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        final String date = String.format(Locale.ENGLISH, "%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));

        mDatabase.child("timetable")
                .child(date)
                .child(moduleId)
                .child(sessionId)
                .child("status")
                .setValue("Open");

        mDatabase.child("module_session")
                .child(moduleId)
                .child(sessionId)
                .child("status")
                .setValue("Open");

        Log.d(LOG_TAG, "Updating records");
        mDatabase.child("modules").child(moduleId).child("students").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<Map<String, String>> t = new GenericTypeIndicator<Map<String, String>>() {};
                Map<String, String> studentMap = snapshot.getValue(t);
                for (String student : studentMap.values()) {
                    Log.d(LOG_TAG, student);
                    mDatabase.child("module_record")
                            .child(moduleId)
                            .child(sessionId)
                            .child(student)
                            .setValue(false);

                    mDatabase.child("student_record")
                            .child(student)
                            .child(moduleId)
                            .child(sessionId)
                            .setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        }, 2000);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onDestroy() {
        NotificationManagerCompat.from(this).cancel(SERVICE_NOTIFICATION_ID);
        Intent intent = new Intent(OpenAttendanceService.this, PeripheralService.class);
        intent.putExtra("sessionId", sessionId);
        intent.putExtra("moduleId", moduleId);
        startService(intent);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
