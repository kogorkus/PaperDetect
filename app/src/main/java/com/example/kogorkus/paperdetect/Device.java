package com.example.kogorkus.paperdetect;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.Locale;

public class Device implements Serializable {
    private String ID, type = "", measurement = "", percent, name, info = "Not selected";
    private Double currentValue, totalValue;
    private int icon;
    private FirebaseDatabase database;
    private MainActivity.myAdapter adapter;


    Device(String ID, String name, final MainActivity.myAdapter adapter) {

        this.adapter = adapter;
        this.ID = ID;
        this.name = name;
        database = FirebaseDatabase.getInstance();
        DatabaseReference Ref = database.getReference("Devices/" + ID);
        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                type = dataSnapshot.child("Type").getValue(String.class);
                if (type.equals("paper")) {
                    icon = R.drawable.ic_toilet_paper;
                    measurement = "m";
                } else if (type.equals("soap")) {
                    icon = R.drawable.ic_liquid_soap;
                    measurement = "ml";
                }

                if (dataSnapshot.child("TotalValue").getValue(Double.class) != null) {
                    try {
                        totalValue = dataSnapshot.child("TotalValue").getValue(Double.class);
                    } catch (Exception e) {
                        Log.e("ReadExc", e.toString());
                    }
                } else totalValue = null;

                if (dataSnapshot.child("CurrentValue").getValue(Double.class) != null) {
                    try {
                        currentValue = dataSnapshot.child("CurrentValue").getValue(Double.class);
                    } catch (Exception e) {
                        Log.e("ReadExc", e.toString());
                    }
                } else currentValue = null;

                if (currentValue != null && totalValue != null) {
                    try {
                        percent = String.format(Locale.getDefault(), "%.2f", currentValue / totalValue * 100) + "%";
                        info = currentValue + measurement + " (" + percent + ")";
                    } catch (NumberFormatException e) {
                        Log.e("parse", "value is incorrect");
                    }
                } else {
                    info = "Not selected";
                }

                Log.d("pic", icon + "");
                UpdateListData();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void UpdateListData() {
        adapter.notifyDataSetChanged();
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

    public String getInfo() {
        return info;
    }

    public Double getTotalValue() {
        return totalValue;
    }

    public String getMeasurement() {
        return measurement;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public String getPercent() {
        return percent;
    }
}
