package com.example.shashanksingh.pack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

import static com.example.shashanksingh.pack.LandingActivity.MyPREFERENCES;

public class DetailArticleActivity extends AppCompatActivity {

    ACProgressFlower mDialog;
    String mUrl = "https://nameless-lowlands-50285.herokuapp.com/api/article/";
    String mImageUrl;
    String mTitle, mBody;
    private Toolbar mToolbar;
    private TextView mArticleTitle, mContent;
    private ImageView mArticleImage;
    private static final String TAG = "DetailArticleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_article);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        mUrl = mUrl.concat(id + "/");

        mToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mArticleTitle = (TextView) findViewById(R.id.article_title);
        mContent = (TextView) findViewById(R.id.article_content);

        mArticleImage = (ImageView) findViewById(R.id.article_image);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailArticleActivity.this, MainActivity.class));
            }
        });

        mDialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.DKGRAY).build();

        mToolbar.setBackgroundColor(getResources().getColor(R.color.primary_500));

        loadArticle();
    }

    public void loadArticle() {

        mDialog.show();

        SharedPreferences sharedPreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final String token = sharedPreferences.getString("user_token", "");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, mUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: " + response.toString());
                try {
                    if (response.getBoolean("success")) {
                        mTitle = response.getString("title");
                        mBody = response.getString("content");
                        mImageUrl = response.getString("image");
                    }

                    mArticleTitle.setText(mTitle);
                    mContent.setText(mBody);
                    Glide.with(getApplicationContext()).load(mImageUrl).into(mArticleImage);

                    mDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDialog.dismiss();
                Log.i("onErrorResponse", error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Token " + token);

                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }
}
