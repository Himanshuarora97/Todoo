package com.doit.todoo.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by himanshu on 19/4/17.
 */

public class DataSource {
    private SQLiteDatabase database;
    private MySQLhelper dbHelper;
    private String[] allColumns = {
            MySQLhelper.COLUMN_ID,
            MySQLhelper.TITLE_TEXT,
            MySQLhelper.CONTENT_TEXT,
            MySQLhelper.COLOR_ID,
            MySQLhelper.PASSOWORD,
            MySQLhelper.IsPassword
    };
    private static DataSource mInstance;
    private Context mContext;

    // used to get the object of DataSource class
    public static synchronized DataSource getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DataSource(context);
        }
        return mInstance;
    }

    public DataSource(Context context) {
        mContext = context;
        dbHelper = MySQLhelper.getInstance(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() throws SQLException {
        dbHelper.close();
    }

    public Model createModel(String model, String content, String color, int isPassword, String password) {
        ContentValues values = new ContentValues();
        values.put(MySQLhelper.TITLE_TEXT, model);
        values.put(MySQLhelper.CONTENT_TEXT, content);
        values.put(MySQLhelper.COLOR_ID, color);
        values.put(MySQLhelper.IsPassword, isPassword);
        values.put(MySQLhelper.PASSOWORD, password);
        long insertID = database.insert(MySQLhelper.TABLE_NAME, null, values);
        Cursor cursor = database.query(MySQLhelper.TABLE_NAME,
                allColumns,
                MySQLhelper.COLUMN_ID + " = " + insertID, null, null, null, null);
        cursor.moveToFirst();
        Model newModel = cursorToModel(cursor);
        cursor.close();
        return newModel;
    }

    private Model cursorToModel(Cursor cursor) {
        Model model = new Model();
        model.setId(cursor.getLong(0));
        model.setTitle(cursor.getString(1));
        model.setContent(cursor.getString(2));
        model.setColor(cursor.getString(3));
        model.setIsPasswordProtected(cursor.getInt(4));
        model.setPassword(cursor.getString(5));
        return model;
    }

    public void deleteComment(Model model) {
        long id = model.getId();
        database.delete(MySQLhelper.TABLE_NAME, MySQLhelper.COLUMN_ID + " = " + id, null);
    }

    public ArrayList<Model> getAllModels() {
        ArrayList<Model> modelList = new ArrayList<Model>();

        Cursor cursor = database.query(MySQLhelper.TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Model model = cursorToModel(cursor);
            modelList.add(model);
            cursor.moveToNext();
        }
        cursor.close();
        return modelList;
    }


    public String getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = mContext.getResources().getIdentifier("colors_" + typeColor, "array", mContext.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = mContext.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return String.format("#%06X", (0xFFFFFF & returnColor));
    }

    public void updateData(Model model) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLhelper.TITLE_TEXT, model.getTitle());
        values.put(MySQLhelper.CONTENT_TEXT, model.getContent());
        values.put(MySQLhelper.COLOR_ID, model.getColor());
        String condition = MySQLhelper.COLUMN_ID + "='" + model.getId() + "'";
        db.update(MySQLhelper.TABLE_NAME, values, condition, null);
    }
}
