package com.example.shashanksingh.pack;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Shashank Singh on 4/11/2017.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_ARTICLE = "articles";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_SOURCE = "source";
    public static final String COLUMN_IMAGE = "image";

    private static final String DATABASE_NAME = "articles.db";
    private static int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_ARTICLE + "( " + COLUMN_ID + " integer primary key, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_CONTENT + " text not null, "
            + COLUMN_SOURCE + " text not null, "
            + COLUMN_IMAGE + " text not null);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + "to " + newVersion + ", which will destroy " +
                "all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLE);
        onCreate(db);
    }
}


















