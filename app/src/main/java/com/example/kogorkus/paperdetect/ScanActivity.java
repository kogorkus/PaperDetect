package com.example.kogorkus.paperdetect;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;

public class ScanActivity extends Activity {

    private CameraSource cameraSource;
    private TextView textView;
    private String length;
    private String code;
    private FirebaseFirestore db;
    private boolean found = false;
    private DBManager dbManager;
    private Cursor cursor;
    private String ScanTarget;
    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        SurfaceView surfaceView = findViewById(R.id.cameraprewiev);
        textView = findViewById(R.id.mTextView);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        if(intent.hasExtra("ScanTarget"))
        {
            ScanTarget = intent.getStringExtra("ScanTarget");
        }

        database = FirebaseDatabase.getInstance();
        db = FirebaseFirestore.getInstance();

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .setAutoFocusEnabled(true).build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                    return;
                }
                try {
                    cameraSource.start(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {

                    code = barcodes.valueAt(0).displayValue;

                    if (ScanTarget.equals("NewDevice"))
                    {
                        textView.setText(code);
                    }
                    if (ScanTarget.equals("ValueFromBase")) {
                        db.collection("test").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                for (DocumentSnapshot snapshot : documentSnapshots) {

                                    if (snapshot.get("Code").equals(code)) {
                                        length = snapshot.get("Length").toString();
                                        textView.setText(snapshot.get("Name").toString());
                                        found = true;
                                    }
                                }
                            }
                        });
                        if (!found) {
                            dbManager = DBManager.getInstance(ScanActivity.this);
                            cursor = dbManager.getAllResults();
                            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                                String Code = cursor.getString(cursor.getColumnIndex("CODE"));
                                if (Code.equals(code)) {
                                    length = cursor.getString(cursor.getColumnIndex("LENGTH"));
                                    textView.setText(cursor.getString(cursor.getColumnIndex("NAME")));
                                    found = true;
                                }

                            }
                        }
                        if (!found) {
                            textView.setText(code);
                            length = "";
                        }
                    }


                }
            }
        });
    }

    public void AddBarcode(View view) {

        if ( ScanTarget.equals("ValueFromBase")) {
            if (found) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("Value", length);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                LayoutInflater li = LayoutInflater.from(this);
                View dialogView = li.inflate(R.layout.not_found_scan_dialog, null);
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
                mDialogBuilder
                        .setView(dialogView)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ScanActivity.this, AddActivity.class);
                                intent.putExtra("code", code);
                                startActivity(intent);
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
        if (ScanTarget.equals("NewDevice")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("DeviceID", code);
            setResult(RESULT_OK, intent);
            finish();
        }


    }
}

