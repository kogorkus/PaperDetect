package com.example.kogorkus.paperdetect;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private TextView textView;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.MainText);

        mAuth = FirebaseAuth.getInstance();

        FirebaseMessaging.getInstance().subscribeToTopic("News")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "subscribed";
                        if (!task.isSuccessful()) {
                            msg = "subscribe failed";
                        }
                        Log.d("Sub", msg);
                    }
                });



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

            database = FirebaseDatabase.getInstance();
            DatabaseReference lengthRef = database.getReference("ESP8266_Test");
            lengthRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String lengthValue = dataSnapshot.child("CurrentLength").getValue(String.class);
                    String percentValue = dataSnapshot.child("Percent").getValue(String.class);
                    if (!lengthValue.equals("") && !lengthValue.equals("0.00")) {
                        textView.setText(lengthValue + " м");
                        if(!percentValue.equals("NaN"))
                        {
                            textView.append("\n (" + percentValue + "%)");
                        }
                    } else {
                        textView.setText(R.string.PleaseSet);
                    }
                    Log.d("db", "Value is: " + lengthValue);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("db", "Failed to read value.", error.toException());
                }
            });

    }

    public void OpenListActivity(View view) {
        Intent intent = new Intent(this, ListActivity.class);
        startActivityForResult(intent, 1);
    }


    public void OpenScanActivity(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String length = data.getStringExtra("length");
        textView.setText(length);
        SetLength(length);
    }

    public void ShowManuallyDialog(View view) {
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.manually_dialog, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        final EditText userInput = dialogView.findViewById(R.id.input_text);
        mDialogBuilder
                .setView(dialogView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String length = userInput.getText() + "";
                            Double.parseDouble(length);
                            textView.setText(length);
                            SetLength(length);
                        } catch (NumberFormatException e) {
                            Toast.makeText(MainActivity.this, "Please enter correct number", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.remove_length) {
            DatabaseReference myRef = database.getReference("ESP8266_Test/Data");
            myRef.setValue(0);
            SetLength("");
        }
        else if (item.getItemId() == R.id.sign_out)
        {
            mAuth.signOut();
            finish();
            startActivity(new Intent(MainActivity.this, AuthActivity.class));

        }
        else if (item.getItemId() == R.id.credits)
        {

        }

        return super.onOptionsItemSelected(item);
    }


    public void SetLength(String length)
    {
        DatabaseReference myRef = database.getReference("ESP8266_Test/Length");
        myRef.setValue(length);
        myRef = database.getReference("ESP8266_Test/CurrentLength");
        myRef.setValue(length);
    }
}
