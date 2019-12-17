package com.example.radarpettracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RegistroSQLHelper extends SQLiteOpenHelper {

    private static final String NAME_DATABASE = "dbRegister";
    private static final int VERSION_DATABASE = 1;

    public static final String TABLE_REGISTER = "register";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_UUID = "uuid";
    public static final String COLUMN_IMEI = "imei";
    public static final String COLUMN_TIMELINE = "timeline";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    public RegistroSQLHelper(Context context) {
        super(context, NAME_DATABASE, null, VERSION_DATABASE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE " + TABLE_REGISTER + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_UUID + " TEXT NOT NULL, " +
                COLUMN_IMEI + " TEXT NOT NULL, " +
                COLUMN_TIMELINE + " TEXT NOT NULL, " +
                COLUMN_LATITUDE + " TEXT NOT NULL, " +
                COLUMN_LONGITUDE + " TEXT NOT NULL);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
