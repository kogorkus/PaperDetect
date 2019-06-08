package com.example.kogorkus.paperdetect;




import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Device {
    private String ID, type, measurement, name;
    private double currentValue, percent;
    private int icon;
    private FirebaseDatabase database;


    Device(String ID, String name, final MainActivity.myAdapter adapter) {
        database = FirebaseDatabase.getInstance();
        this.ID = ID;
        this.name = name;
        DatabaseReference Ref = database.getReference("Devices/" + ID);
        Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                type = dataSnapshot.child("Type").getValue(String.class);
                if (type.equals("paper")) {
                    icon = R.drawable.ic_toilet_paper;
                } else if (type.equals("soap")) {
                    icon = R.drawable.ic_liquid_soap;
                }
                Log.d("pic", icon + "");
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    public int getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
    }

    public String getType() {
        return type;
    }
}
