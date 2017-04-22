package com.example.shashanksingh.pack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Shashank Singh on 4/11/2017.
 */

public class ArticlesDataSource {

    private SQLiteDatabase mDatabase;
    private MySQLiteHelper mDbHelper;
    private String[] mAllArticles = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_TITLE, MySQLiteHelper.COLUMN_CONTENT,
                                     MySQLiteHelper.COLUMN_SOURCE, MySQLiteHelper.COLUMN_IMAGE};

    public ArticlesDataSource(Context context) {
        mDbHelper = new MySQLiteHelper(context);
    }

    public void createArticle(String id, String title, String content, String source, String image) {
        mDatabase = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ID, Integer.valueOf(id));
        values.put(MySQLiteHelper.COLUMN_TITLE, title);
        values.put(MySQLiteHelper.COLUMN_CONTENT, content);
        values.put(MySQLiteHelper.COLUMN_SOURCE, source);
        values.put(MySQLiteHelper.COLUMN_IMAGE, image);

        long rowInserted = mDatabase.insert(MySQLiteHelper.TABLE_ARTICLE, null, values);
        Log.d(TAG, "createArticle: " + rowInserted);

//        Cursor cursor = mDatabase.query(MySQLiteHelper.TABLE_ARTICLE, mAllArticles, null, null, null, null, null);
//        cursor.moveToFirst();
//        Article newArticle = cursorToArticle(cursor);
//        cursor.close();
        mDbHelper.close();
    }

    public void deleteArticle(Article article) {
        String id = article.getId();
        mDatabase.delete(MySQLiteHelper.TABLE_ARTICLE, MySQLiteHelper.COLUMN_ID + " = " + Integer.valueOf(id), null);
    }

    public List<Article> getAllArticles() {
        mDatabase = mDbHelper.getReadableDatabase();
        mDatabase.beginTransaction();
        List<Article> articles = new ArrayList<>();
        Cursor cursor = mDatabase.query(MySQLiteHelper.TABLE_ARTICLE, mAllArticles, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Article article = cursorToArticle(cursor);
            articles.add(article);
            cursor.moveToNext();
        }
        cursor.close();
        mDatabase.endTransaction();
        mDatabase.close();
        return articles;
    }

    private Article cursorToArticle(Cursor cursor) {
        Log.d(TAG, "cursorToArticle: " + cursor);
        Article article = new Article();
        article.setId(cursor.getString(0));
        article.setTitle(cursor.getString(1));
        article.setBody(cursor.getString(2));
        article.setImage(cursor.getString(4));
        return article;
    }
}











