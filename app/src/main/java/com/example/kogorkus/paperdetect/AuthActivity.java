package com.example.kogorkus.paperdetect;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText PassET, MailET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        PassET = findViewById(R.id.PassET);
        MailET = findViewById(R.id.MailET);

        mAuth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (user.isEmailVerified()) {
                        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }


    public void SignUp(View view) {
        if (!MailET.getText().toString().equals("") && !PassET.getText().toString().equals(""))
            mAuth.createUserWithEmailAndPassword(MailET.getText().toString(), PassET.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AuthActivity.this, "Signing up successful", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                user.sendEmailVerification();
                                Toast.makeText(AuthActivity.this, "Please, verify your Email", Toast.LENGTH_LONG).show();
                            } else {

                                Toast.makeText(AuthActivity.this, "Signing up failed." +
                                                task.getException().toString()
                                                        .split(":")[1],
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });

    }

    public void SignIn(View view) {
        if (!MailET.getText().toString().equals("") && !PassET.getText().toString().equals(""))
            mAuth.signInWithEmailAndPassword(MailET.getText().toString(), PassET.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (task.isSuccessful()) {
                                if (user.isEmailVerified()) {
                                    Toast.makeText(AuthActivity.this, "Signing in successful",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(AuthActivity.this, "Email is not Verified! Check your Email",
                                            Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Toast.makeText(AuthActivity.this, "Signing in failed." +
                                                task.getException().toString().split(":")[1],
                                        Toast.LENGTH_LONG).show();
                            }
                        }


                    });
    }
}
