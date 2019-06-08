package com.example.kogorkus.paperdetect;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private String id;
    private FirebaseDatabase database;
    private myAdapter adapter;
    private ListView listView;
    private FirebaseAuth mAuth;
    private ArrayList<Device> arrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.devicesLV);

        arrayList = new ArrayList<>();
        adapter = new myAdapter(arrayList);
        listView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        id = user.getUid();

        database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users/" + id);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String deviceID = snapshot.getValue().toString();
                    String deviceName = snapshot.getKey();
                    arrayList.add(new Device(deviceID, deviceName, adapter));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        /*
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
        */


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
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
        }); */

    }



    /*
    public void ShowManuallyDialog(View view) {
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.dialog, null);
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
    } */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.sign_out)
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

    class myAdapter extends ArrayAdapter<Device> {
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.device_list_item, null);
            Device temp = arrayList.get(position);
            ImageView imageView = v.findViewById(R.id.DeviceIcon);
            TextView nameTV = v.findViewById(R.id.NameOfDevice);
            nameTV.setText(temp.getName());
            imageView.setImageResource(temp.getIcon());
            Log.d("pic", temp.getIcon() + "");
            return v;
        }

        public myAdapter(ArrayList<Device> arrayList) {
            super(MainActivity.this, R.layout.device_list_item, arrayList);
        }
    }

    public void AddNewDevice (View view)
    {
        Intent intent = new Intent(MainActivity.this, ScanActivity.class);
        intent.putExtra("ScanTarget", "NewDevice");
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            final String deviceID = data.getStringExtra("DeviceID");
            Log.d("jojo", deviceID);
            DatabaseReference deviceRef = database.getReference("Devices");
            deviceRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean found = false;
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                         if(deviceID.equals(snapshot.getKey()))
                         {
                             found = true;
                         }
                         else
                         {

                         }

                         Log.d("jojo", snapshot.getKey() + " " + found);
                    }
                    if (found)
                    {
                        View parentLayout = findViewById(android.R.id.content);
                        ShowAddDeviceDialog(parentLayout, deviceID);
                    }
                    else
                    {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    public void ShowAddDeviceDialog(View view, final String DeviceID) {
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.dialog, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        final EditText userInput = dialogView.findViewById(R.id.input_text);
        TextView dialogDescriptionTV = dialogView.findViewById(R.id.tv);
        dialogDescriptionTV.setText("Enter name of new Device. Please dont use existing names, it will replace existing Device");
        mDialogBuilder
                .setView(dialogView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference userRef = database.getReference("users/" + id + "/" + userInput.getText().toString());
                        userRef.setValue(DeviceID);
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




}
