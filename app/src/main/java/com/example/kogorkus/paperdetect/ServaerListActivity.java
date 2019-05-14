package com.example.kogorkus.paperdetect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ServaerListActivity extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> productList;
    private ListView listView;
    private String jsonData;
    private String urlAddress="http://phasmid-helmet.000webhostapp.com/package/android.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servaer_list);
        listView = findViewById(R.id.List);
        Downloader downloader = new Downloader(urlAddress);
        downloader.execute();
        do {
            jsonData = downloader.getJsonData();
        } while (jsonData == null);/*
        DataParser dataParser = new DataParser(jsonData);
        dataParser.execute();
        do {
            productList = dataParser.getProductList();
        } while (productList == null);*/
        DataParser dataParser = new DataParser(ServaerListActivity.this, jsonData, listView);
        dataParser.execute();
        do {
            productList = dataParser.getProductList();
        } while (!dataParser.ThatsOver);
        Log.d("SUKA", productList.toString());

        ListAdapter adapter = new SimpleAdapter(
                ServaerListActivity.this, productList,
                R.layout.listitem, new String[] { "NAME", "LENGTH" }, new int[] { R.id.NameTV,
                R.id.LengthTV});
        listView.setAdapter(adapter);
    }
}
