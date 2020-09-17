package com.example.jakec.smart_key;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jakec on 09/03/2019.
 */

public class KeyDataDB extends SQLiteOpenHelper {

    private static int DATABASE_VERSION = 2;

    private static String DATABASE_NAME = "KeyDataDB";

    private static String TABLE_VEHICLES = "Keys";

    private static final String KEY_VEHID = "VehicleID";
    private static final String KEY_KEYID = "KeyID";
    private static final String KEY_NAME = "Name";
    private static final String KEY_KEYDATA = "KeyData";
    private static final String KEY_ROLLER = "Roller";
    private static final String KEY_ENCRYPT = "Encryption_Key";
    private static final String KEY_TEMP = "Temp";
    private static final String KEY_ACTIVE = "Active";
    private static final String KEY_ENDDATE = "End";

    SQLiteDatabase db;
    Context context;

    public KeyDataDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_KEYS_TABLE = "Create Table  "+TABLE_VEHICLES+" ("+ KEY_KEYID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+ KEY_NAME+ " TEXT,"+ KEY_KEYDATA+" TEXT," +KEY_ENCRYPT+" TEXT, "+ KEY_ROLLER+" INTEGER,"+ KEY_TEMP+" INTEGER,"+ KEY_ACTIVE+" INTEGER,"+ KEY_ENDDATE+" TEXT)";
        db.execSQL(CREATE_KEYS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<AndroidKeyData> getAllActiveKeys(){
        List<AndroidKeyData> keys = new ArrayList<AndroidKeyData>();
        String selectQuery = "SELECT " + KEY_KEYID + ", " + KEY_NAME + ", " + KEY_KEYDATA + ", " + KEY_ENCRYPT + ", " + KEY_ROLLER + ", " + KEY_TEMP + ", " + KEY_ACTIVE+ ", " + KEY_ENDDATE + " FROM " + TABLE_VEHICLES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                System.out.println("Key Found");
                AndroidKeyData tmpKey = new AndroidKeyData();
                tmpKey.setKey_ID(cursor.getInt(cursor.getColumnIndex(KEY_KEYID)));
                tmpKey.setVehName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                tmpKey.setKeyData(cursor.getString(cursor.getColumnIndex(KEY_KEYDATA)));
                tmpKey.setEncryptionKey(cursor.getString(cursor.getColumnIndex(KEY_ENCRYPT)));
                tmpKey.setKeyRoller(cursor.getInt(cursor.getColumnIndex(KEY_ROLLER)));
                tmpKey.setTemp(cursor.getInt(cursor.getColumnIndex(KEY_TEMP)));
                tmpKey.setEnd(cursor.getString(cursor.getColumnIndex(KEY_ENDDATE)));
                keys.add(tmpKey);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return keys;
    }



    public boolean addKey(AndroidKeyData key){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean ret = true;
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, key.getVehName());
            values.put(KEY_KEYDATA, new String(key.getKeyData()));
            values.put(KEY_ENCRYPT, key.getEncryptionKey().toString());
            values.put(KEY_ROLLER, key.getKeyRoller());
            values.put(KEY_ENDDATE, new String(" "+String.valueOf(key.getEnd().getTimeInMillis())));
            values.put(KEY_KEYID, key.getVeh_ID());
            values.put(KEY_TEMP, key.isTemp());
            db.insert(TABLE_VEHICLES, null, values);
            Toast.makeText(context, "Key added", Toast.LENGTH_LONG).show();
        } catch (Exception e){
            //System.out.println( e.toString());
            Toast.makeText(context, "Key has already been added?", Toast.LENGTH_SHORT).show();
            ret = false;
        }
        db.close();
        return ret;
    }

    public boolean keyPresent(int id){
        String selectQuery = "SELECT " + KEY_KEYID + " FROM " + TABLE_VEHICLES +" where "+KEY_KEYID + " = ?";
        String[] args = {""+id};
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, args);
        System.out.println("Checking prescence of : "+id);
        if (cursor.moveToFirst()) {
            do {
                System.out.println("Checking prescence of : "+id);
                cursor.close();
                db.close();
                return true;
            } while (cursor.moveToNext());
        }
        System.out.println("Checking prescence of : "+id);
        cursor.close();
        db.close();
        return false;
    }


    public boolean deleteKey(int id){
        String selectQuery = "DELETE  FROM " + TABLE_VEHICLES +" where "+KEY_KEYID + " = ?";
        String[] args = {""+id};
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, args);
        if (cursor.moveToFirst()) {
            do {
                cursor.close();
                db.close();
                return true;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return true;
    }


    public AndroidKeyData getKey(int id) {
        AndroidKeyData tmpKey = null;
        String selectQuery = "SELECT " + KEY_KEYID + ", " + KEY_NAME + ", " + KEY_KEYDATA + ", " + KEY_ENCRYPT + ", " + KEY_ROLLER + ", " + KEY_TEMP + ", " + KEY_ACTIVE+ ", " + KEY_ENDDATE + " FROM " + TABLE_VEHICLES+" where "+KEY_KEYID + " = ?";
        String[] args = {""+id};

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, args);

        if (cursor.moveToFirst()) {
            do {
                tmpKey = new AndroidKeyData();
                System.out.println("Key Found");
                tmpKey.setKey_ID(cursor.getInt(cursor.getColumnIndex(KEY_KEYID)));
                tmpKey.setVehName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                tmpKey.setKeyData(cursor.getString(cursor.getColumnIndex(KEY_KEYDATA)));
                tmpKey.setEncryptionKey(cursor.getString(cursor.getColumnIndex(KEY_ENCRYPT)));
                tmpKey.setKeyRoller(cursor.getInt(cursor.getColumnIndex(KEY_ROLLER)));
                tmpKey.setTemp(cursor.getInt(cursor.getColumnIndex(KEY_TEMP)));
                tmpKey.setEnd(cursor.getString(cursor.getColumnIndex(KEY_ENDDATE)));
                //tmpKey.setValid();
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tmpKey;
    }
}
