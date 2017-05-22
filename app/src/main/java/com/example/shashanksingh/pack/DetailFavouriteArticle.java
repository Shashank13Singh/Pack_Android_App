package com.example.shashanksingh.pack;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;

/**
 * Created by Shashank Singh on 4/21/2017.
 */

public class DetailFavouriteArticle extends AppCompatActivity{

    String mTitle, mBody, mImage;
    private Toolbar mToolbar;
    private TextView mArticleTitle, mContent;
    private ImageView mArticleImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_article);

        Intent intent = getIntent();
        mTitle = intent.getStringExtra("title");
        mBody = intent.getStringExtra("body");
        mImage = intent.getStringExtra("image");

        ContextWrapper contextWrapper = new ContextWrapper(this);

        File directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);
        String path = directory.getAbsolutePath();

        path = path + "/" + mImage;

        mToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mArticleTitle = (TextView) findViewById(R.id.article_title);
        mContent = (TextView) findViewById(R.id.article_content);

        mArticleImage = (ImageView) findViewById(R.id.article_image);

        mArticleTitle.setText(mTitle);
        mContent.setText(mBody);

        Glide.with(this)
                .load(path)
                .into(mArticleImage);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mToolbar.setBackgroundColor(getResources().getColor(R.color.primary_500));

    }
}
