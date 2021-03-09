package com.example.beam;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LecturerActivity extends AppCompatActivity {

    String currentModuleID;
    String currentModuleName;
    boolean open = false;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    Button logoutButton;
    String currentSessionID;
    String currentSessionType;
    String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    DatabaseReference database= FirebaseDatabase.getInstance().getReference();
    ZonedDateTime date = ZonedDateTime.now(ZoneId.of("Asia/Singapore"));
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    String currentDate = date.format(formatter);
    ArrayList<String> lecturerModuleID = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * Logout Button
         */
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        logoutButton = (Button) findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                redirectToLoginActivity();
            }
        });
        /**
         * Open Attendance
         */
        Button Open_Attendance = findViewById(R.id.open_attendance);
        Open_Attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLecturerModuleID();
                checkIfClassInSession(view);
            }
        });

        /**
         * Close Attendance
         */
        Button closeAttendance = findViewById(R.id.close_attendance);
        closeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeAttendance();
            }
        });
    }

    // Working, set the modules of the lecturer
    public void setLecturerModuleID() {
        DatabaseReference ref = database.child("users/" + currentUserID + "/modules/");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String moduleID = ds.getValue(String.class);
                    lecturerModuleID.add(moduleID);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        ref.addListenerForSingleValueEvent(eventListener);
    }

    // Working, check if the lecturer has a class in session
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void checkIfClassInSession(final View view) {
        for (final String moduleID : lecturerModuleID) {
            final DatabaseReference ref = database.child("timetable/" + currentDate + "/" + moduleID);
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    LocalTime currentTime = LocalTime.now();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String sessionID =  ds.child("sessionID").getValue(String.class);
                        String sessionTimeBegin = ds.child("timeBegin").getValue(String.class);
                        assert sessionTimeBegin != null;
                        LocalTime timeBegin = LocalTime.of(Integer.parseInt(sessionTimeBegin.substring(0, 2)), Integer.parseInt(sessionTimeBegin.substring(2, 4)));
                        String sessionTimeEnd =  ds.child("timeEnd").getValue(String.class);
                        assert sessionTimeEnd != null;
                        LocalTime timeEnd = LocalTime.of(Integer.parseInt(sessionTimeEnd.substring(0, 2)), Integer.parseInt(sessionTimeEnd.substring(2, 4)));
                        if (currentTime.isAfter(timeBegin) && (currentTime.isBefore(timeEnd))) {
                            currentSessionID = sessionID;
                            currentModuleID = moduleID;
                            currentSessionType = ds.child("sessionType").getValue(String.class);
                            openAttendancePopUp(view);
                            break;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            ref.addListenerForSingleValueEvent(eventListener);
            if (open) {
                Toast.makeText(LecturerActivity.this,currentModuleName + " (" + currentSessionType + " ) " + currentSessionID , Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void openAttendancePopUp(final View view) {
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        boolean focusable = true;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        TextView test2 = popupView.findViewById(R.id.sessionTextView);
        test2.setText(currentModuleName + " (" + currentSessionType + " ) in session - " + currentSessionID + ". Open Attendance?");

        Button open = popupView.findViewById(R.id.open);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenAttendance();
            }
        });

        FloatingActionButton dismiss = popupView.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });


        final DatabaseReference ref = database.child("modules/" + currentModuleID);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentModuleName = dataSnapshot.child("name").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref.addListenerForSingleValueEvent(eventListener);
    }


    public void closeAttendance() {
        DatabaseReference ref = database.child("timetable/" + currentDate + "/" + currentModuleID + "/" + currentSessionID);
        ref.child("status").setValue("Closed");
    }

    private void redirectToLoginActivity() {
        Intent loginIntent = new Intent(LecturerActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }

    public void OpenAttendance() {
        DatabaseReference ref = database.child("timetable/" + currentDate + "/" + currentModuleID + "/" + currentSessionID);
        ref.child("status").setValue("Open");
    }
}
