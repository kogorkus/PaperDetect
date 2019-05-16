package com.example.kogorkus.paperdetect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class AddActivity extends Activity {

    private EditText CodeET, NameET, LengthET;
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        dbManager = DBManager.getInstance(this);
        CodeET = findViewById(R.id.editText);
        NameET = findViewById(R.id.editText2);
        LengthET = findViewById(R.id.editText3);

        Bundle extras = getIntent().getExtras();
        String code;
        if (extras != null) {
            code = extras.getString("code");
            CodeET.setText(code);
        }

        Button addButton = findViewById(R.id.button4);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbManager.addBarcode(CodeET.getText().toString(), NameET.getText().toString(), LengthET.getText().toString());
                CodeET.setText("");
                NameET.setText("");
                LengthET.setText("");
            }
        });


    }

    public void Scan(View view) {
        Intent intent = new Intent(this, ScanToAddActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String code = data.getStringExtra("code");
        CodeET.setText(code);
    }

}