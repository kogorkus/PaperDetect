package com.example.kogorkus.paperdetect;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private myAdapter adapter;
    private ArrayList<String[]> arrayList = new ArrayList<>();
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = findViewById(R.id.list_toolbar);
        setSupportActionBar(toolbar);

        final ListView listView = findViewById(R.id.myList);
        adapter = new myAdapter(arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String length = arrayList.get(position)[1];
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                intent.putExtra("length", length);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                dbManager.deleteBarcode(arrayList.get(position)[0]);
                arrayList.remove(position);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        dbManager = DBManager.getInstance(this);
        Cursor cursor = dbManager.getAllResults();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("NAME"));
            String length = cursor.getString(cursor
                    .getColumnIndex("LENGTH"));
            arrayList.add(new String[]{name, length});
        }


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("test").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentSnapshot snapshot : documentSnapshots){
                    adapter.notifyDataSetChanged();
                    arrayList.add(new String[]{snapshot.get("Name") + "", snapshot.get("Length") + ""});
                }
            }
        });
    }

    public void OpenScanActivity(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, 1);
        finish();

    }

    class myAdapter extends ArrayAdapter<String[]> {
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v = LayoutInflater.from(ListActivity.this).inflate(R.layout.list, null);
            TextView tvName =  v.findViewById(R.id.Name);
            TextView tvLength = v.findViewById(R.id.Length);
            tvName.setText(arrayList.get(position)[0]);
            tvLength.setText(arrayList.get(position)[1]);

            return v;
        }

        public myAdapter(ArrayList<String[]> arrayList) {
            super(ListActivity.this, R.layout.list, arrayList);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add_to_local_db)
        {
            Intent intent = new Intent(this, AddActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        adapter.notifyDataSetChanged();
        super.onRestart();
    }
}
