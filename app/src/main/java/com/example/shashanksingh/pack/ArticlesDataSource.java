package com.example.shashanksingh.pack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

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

    public Article createArticle(String id, String title, String content, String source, String image) {
        mDatabase = mDbHelper.getWritableDatabase();
        mDatabase.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ID, id);
        values.put(MySQLiteHelper.COLUMN_TITLE, title);
        values.put(MySQLiteHelper.COLUMN_CONTENT, content);
        values.put(MySQLiteHelper.COLUMN_SOURCE, source);
        values.put(MySQLiteHelper.COLUMN_IMAGE, image);

        mDatabase.insert(MySQLiteHelper.TABLE_ARTICLE, null, values);
        Cursor cursor = mDatabase.query(MySQLiteHelper.TABLE_ARTICLE, mAllArticles, null, null, null, null, null);
        cursor.moveToFirst();
        Article newArticle = cursorToArticle(cursor);
        cursor.close();
        mDatabase.endTransaction();
        mDbHelper.close();
        return  newArticle;
    }

    public void deleteArticle(Article article) {
        String id = article.getId();
        mDatabase.delete(MySQLiteHelper.TABLE_ARTICLE, MySQLiteHelper.COLUMN_ID + " = " + id, null);
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
        Article article = new Article();
        article.setId(cursor.getString(0));
        article.setTitle(cursor.getString(1));
        article.setBody(cursor.getString(2));
        article.setImage(cursor.getString(4));
        return article;
    }
}











