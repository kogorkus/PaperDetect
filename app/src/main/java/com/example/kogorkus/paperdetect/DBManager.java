package com.example.kogorkus.paperdetect;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DBManager {
    private Context context;
    private String DB_NAME = "barcodes.db";

    private SQLiteDatabase db;

    private static DBManager dbManager;

    public static DBManager getInstance(Context context) {
        if (dbManager == null) {
            dbManager = new DBManager(context);
        }
        return dbManager;
    }

    private DBManager(Context context) {
        this.context = context;
        db = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        createTablesIfNeedBe();
    }

    private void createTablesIfNeedBe() {
        db.execSQL("CREATE TABLE IF NOT EXISTS BARCODES (CODE TEXT, NAME TEXT);");
    }

    void addBarcode(String barcode, String name) {
        db.execSQL("INSERT INTO BARCODES VALUES ('" + barcode + "', '" + name
                + "');");
    }

    ArrayList<Barcode> getAllResults() {

        ArrayList<Barcode> data = new ArrayList<Barcode>();
        Cursor cursor = db.rawQuery("SELECT * FROM BARCODES ORDER BY CODE DESC;", null);
        boolean hasMoreData = cursor.moveToFirst();

        while (hasMoreData) {
            String name = cursor.getString(cursor.getColumnIndex("NAME"));
            String code = cursor.getString(cursor
                    .getColumnIndex("CODE"));
            data.add(new Barcode(name, code));
            hasMoreData = cursor.moveToNext();
        }

        return data;
    }
}
