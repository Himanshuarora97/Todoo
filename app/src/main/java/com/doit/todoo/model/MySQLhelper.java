package com.doit.todoo.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by himanshu on 19/4/17.
 */

public class MySQLhelper extends SQLiteOpenHelper {

    private static MySQLhelper mInstance;
    public static final String TABLE_NAME = "comments";
    public static final String COLUMN_ID = "_id";
    public static final String TITLE_TEXT = "title";
    public static final String CONTENT_TEXT = "content";
    public static final String COLOR_ID = "color";
    public static final String PASSOWORD = "password";
    public static final String IsPassword = "isPassword";
    public static final String DATABASE_NAME = "comments.db";
    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "( " + COLUMN_ID + " integer primary key autoincrement, "
            + TITLE_TEXT + " text not null, "
            + CONTENT_TEXT + " text, "
            + COLOR_ID + " text, "
            + PASSOWORD + " text, "
            + IsPassword + " INTEGER DEFAULT 0"
            + ");";

    public static synchronized MySQLhelper getInstance(Context context) {
        if (mInstance == null)
            mInstance = new MySQLhelper(context.getApplicationContext());
        return mInstance;
    }

    private MySQLhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP table if exists " + TABLE_NAME);
        onCreate(db);
    }
}
