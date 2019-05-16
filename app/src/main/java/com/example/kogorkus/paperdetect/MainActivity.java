package com.example.kogorkus.paperdetect;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private TextView textView;
    private int NumberOfTerms;
    private double Difference;
    private double TotalLength;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.MainText);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        DatabaseReference lengthRef = database.getReference("length");
        lengthRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = Objects.requireNonNull(dataSnapshot.getValue(String.class));
                if (!value.equals("")) {
                    TotalLength = Double.parseDouble(value);
                    NumberOfTerms = (int) (TotalLength * 100 / ((14 + 35) * 0.5));
                    Difference = ((35 - 14) / (double) NumberOfTerms);
                    textView.setText(value);
                } else {
                    textView.setText(R.string.PleaseSet);
                }
                Log.d("db", "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("db", "Failed to read value.", error.toException());
            }
        });

        DatabaseReference turnRef = database.getReference("ESP8266_Test/Data");
        turnRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Long value = dataSnapshot.getValue(Long.class);
                if (value != 0 && TotalLength != 0) {
                    double var = TotalLength * 100 - (value * 0.5 * (35 + (35 + (value - 1) * -Difference)));
                    textView.setText(String.format(Locale.getDefault(), "%.2f", var / 100));
                    textView.append("\n" + "(" + (String.format(Locale.getDefault(), "%.2f", var / TotalLength)) + "%)");
                    Log.d("db", "Value is: " + value);
                } else {
                    textView.setText(R.string.PleaseSet);
                }
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
        DatabaseReference myRef = database.getReference("length");
        myRef.setValue(length);
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
                            DatabaseReference myRef = database.getReference("length");
                            myRef.setValue(length);
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
            DatabaseReference myRef = database.getReference("length");
            myRef.setValue("");
            myRef = database.getReference("ESP8266_Test/Data");
            myRef.setValue(0);
        }

        return super.onOptionsItemSelected(item);
    }
}
