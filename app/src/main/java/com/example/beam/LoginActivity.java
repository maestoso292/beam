package com.example.beam;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText;
    EditText passwordEditText;
    Button loginButton;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailEditText = (EditText) findViewById(R.id.email);
        passwordEditText = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login_button);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    /**
     * Method for login
     */
    private void login() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(LoginActivity.this,"Email",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(LoginActivity.this,"Password",Toast.LENGTH_SHORT).show();
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                redirectToMainActivity();
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this,"Error: "+message,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * If user has already signed in, redirect the user without having to sign in again
     */
    protected void onStart() {
        super.onStart();
        if(currentUser!=null)
        {
            redirectToMainActivity();
        }
    }

    /**
     * Redirect the user to Main Activity
     */
    private void redirectToMainActivity() {
        Intent MainIntent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(MainIntent);
    }
}
