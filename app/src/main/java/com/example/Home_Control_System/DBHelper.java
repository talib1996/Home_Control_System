package com.example.Home_Control_System;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Userdata.db";
    private static final String TABLE_NAME = "userdetails";
    private static final int TABLE_VERSION = 1 ;
    private static final String COLUMN_USER_ID = "user_id";

    private static final String COLUMN_USER_MOBILE_NUMBER = "user_mobile_number";
    private static final String COLUMN_USER_PASSWORD = "user_password";

    // create table sql query
    private String CREATE_USER_TABLE;
    SQLiteDatabase db ;


    {
        CREATE_USER_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                 + COLUMN_USER_MOBILE_NUMBER + "TEXT PRIMARY KEY NOT NULL, "
                + COLUMN_USER_PASSWORD + " TEXT NOT NULL)";
    }



    // drop table sql query
    private String DROP_USER_TABLE;

    {

        DROP_USER_TABLE =  "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, TABLE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        this.db = db;

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_USER_TABLE);
        this.onCreate(db);
    }

/*
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);

    }
*/

    public long insertData(User user){
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_MOBILE_NUMBER, user.getMobilenumber());

        contentValues.put(COLUMN_USER_PASSWORD, user.getPassword());
         return db.insert(TABLE_NAME,null ,contentValues);
    }
    public Cursor validateuser(User user){
        SQLiteDatabase db = this.getReadableDatabase();
        String num = user.getMobilenumber();
        String pwd = user.getPassword();
       String selection = "SELECT * FROM  userdetails  WHERE "+
               COLUMN_USER_MOBILE_NUMBER +" = '" + num +"'";
       Cursor cursor = db.rawQuery(selection, null);
       /*
       Cursor cur = db.query(
               TABLE_NAME,
               projection,
               selection,
               selectionargs,
               null,
               null,
               null

       );
       */
       return  cursor;
    }

}
