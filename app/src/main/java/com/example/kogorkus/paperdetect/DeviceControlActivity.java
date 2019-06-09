package com.example.kogorkus.paperdetect;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;

public class DeviceControlActivity extends AppCompatActivity {

    private Device device;
    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);

        ImageView iconIV = findViewById(R.id.iconIV);
        TextView nameTV = findViewById(R.id.NameTV);
        TextView totalTV = findViewById(R.id.TotalTV);
        TextView currentTV = findViewById(R.id.CurrentTV);

        database = FirebaseDatabase.getInstance();

        device = (Device)((ObjectWrapperForBinder)getIntent().getExtras().getBinder("Device")).getData();

        nameTV.setText(device.getName());
        iconIV.setImageResource(device.getIcon());

        if (device.getTotalValue() != null)
        {
            totalTV.setText("Total: " + device.getTotalValue() +device.getMeasurement());
        }
        else
        {
            totalTV.setText(R.string.not_selected);
        }

        currentTV.setText(device.getInfo());




    }

    public void ClearValue(View view)
    {
        SetValue(null);
    }

    public void SelectValue(View view)
    {
        Intent intent = new Intent(this, ListActivity.class);

        startActivityForResult(intent, 1);
    }



    public void OpenScanActivity(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra("ScanTarget", "ValueFromBase");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String Value = data.getStringExtra("Value");
        SetValue(Double.parseDouble(Value));

    }

    public void ShowManuallyDialog(final View view) {
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.dialog, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        final EditText userInput = dialogView.findViewById(R.id.input_text);
        TextView dialogDescriptionTV = dialogView.findViewById(R.id.tv);
        dialogDescriptionTV.setText(getString(R.string.enter_value));
        mDialogBuilder
                .setView(dialogView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String Value = userInput.getText() + "";
                            SetValue(Double.parseDouble(Value));
                            dialog.cancel();
                        } catch (NumberFormatException e) {
                            Toast.makeText(DeviceControlActivity.this, "Please enter correct number", Toast.LENGTH_SHORT).show();
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

    public void SetValue(Double Value)
    {
        DatabaseReference Ref = database.getReference("Devices/" + device.getID() + "/TotalValue");
        Ref.setValue(Value);
        Ref = database.getReference("Devices/" + device.getID() + "/CurrentValue");
        Ref.setValue(Value);
        device.UpdateListData();
        recreate();
    }

}
