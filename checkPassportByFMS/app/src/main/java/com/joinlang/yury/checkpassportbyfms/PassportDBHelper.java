package com.joinlang.yury.checkpassportbyfms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.AbstractList;
import java.util.ArrayList;

public class PassportDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "fms-service";
    public static final String TABLE_NAME = "passport";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SERIES = "series";
    public static final String COLUMN_NUMBER = "number";
    public static final String COLUMN_RESULT = "result";
    private static final int DATABASE_VERSION = 1;

    public PassportDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME +
                        " ( " +
                        COLUMN_ID + " INTEGER PRIMARY KEY," +
                        COLUMN_SERIES + " TEXT," +
                        COLUMN_NUMBER + " TEXT," +
                        COLUMN_RESULT + " TEXT" +
                        " )"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insert(String series, String number, String result) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SERIES, series);
        contentValues.put(COLUMN_NUMBER, number);
        contentValues.put(COLUMN_RESULT, result);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean update(Integer id, String series, String number, String result) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SERIES, series);
        contentValues.put(COLUMN_NUMBER, number);
        contentValues.put(COLUMN_RESULT, result);
        db.update(TABLE_NAME, contentValues, COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer delete(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    public void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    public Cursor getById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(
                "select * from " + TABLE_NAME +
                        " where " + COLUMN_ID + " = " + id + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public AbstractList<Passport> getAll() {
        ArrayList<Passport> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        cursor.moveToFirst();

        Passport passport;
        while (!cursor.isAfterLast()) {
            passport = new Passport();
            passport.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            passport.setSeries(cursor.getString(cursor.getColumnIndex(COLUMN_SERIES)));
            passport.setNumber(cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER)));
            passport.setResult(cursor.getString(cursor.getColumnIndex(COLUMN_RESULT)));
            arrayList.add(passport);
            cursor.moveToNext();
        }
        return arrayList;
    }


}
