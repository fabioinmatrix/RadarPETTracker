package com.example.radarpettracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.radarpettracker.model.Registro;

import static com.example.radarpettracker.database.RegistroSQLHelper.TABLE_REGISTER;

public class RegistroRepository {

    private RegistroSQLHelper registroSQLHelper;

    public RegistroRepository(Context context) {
        registroSQLHelper = new RegistroSQLHelper(context);
    }

    public long insert(Registro registro) {
        SQLiteDatabase db = registroSQLHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(registroSQLHelper.COLUMN_UUID, registro.getUUID());
        contentValues.put(registroSQLHelper.COLUMN_IMEI, registro.getImei());
        contentValues.put(registroSQLHelper.COLUMN_TIMELINE, registro.getTimeline());
        contentValues.put(registroSQLHelper.COLUMN_LATITUDE, registro.getLatitude());
        contentValues.put(registroSQLHelper.COLUMN_LONGITUDE, registro.getLongitude());

        long id = db.insert(TABLE_REGISTER, null, contentValues);
        if (id != -1) {
            registro.setId(id);
        }
        db.close();
        return id;
    }
}
