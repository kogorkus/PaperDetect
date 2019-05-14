package com.example.kogorkus.paperdetect;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    MyAdapter myAdapter;
    ListView myList;
    private DBManager dbManager;
    private ArrayList<String> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        dbManager = DBManager.getInstance(this);
        ArrayList<Barcode> barcodes = dbManager.getAllResults();

        for (Barcode res : barcodes)
        {
            data.add(res.name + ": " + res.code);
        }
        myList = findViewById(R.id.myList);
        myAdapter = new MyAdapter(data);
        myList.setAdapter(myAdapter);
        myList.setOnItemClickListener(myAdapter);
    }

    public void OpenScanActivity(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, 1);
        finish();

    }

    class MyAdapter extends ArrayAdapter<String> implements AdapterView.OnItemClickListener{

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v = LayoutInflater.from(ListActivity.this).inflate(R.layout.myitem, null);
            TextView tv = (TextView) v.findViewById(R.id.TxtItem);
            tv.setText(data.get(position));
            String str = data.get(position);
            return v;
        }

        public MyAdapter(ArrayList<String> data) {
            super(ListActivity.this, R.layout.myitem, data);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            this.notifyDataSetChanged();
        }
    }
    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dbManager.addBarcode(data.getStringExtra("code"), "name");
        myAdapter.notifyDataSetChanged();
    }
    */
}
