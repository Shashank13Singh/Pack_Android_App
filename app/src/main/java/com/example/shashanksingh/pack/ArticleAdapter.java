package com.example.shashanksingh.pack;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

import static com.android.volley.VolleyLog.TAG;
import static com.example.shashanksingh.pack.LandingActivity.MyPREFERENCES;

/**
 * Created by Shashank Singh on 4/6/2017.
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.MyViewHolder> {

    private Context mContext;
    private List<Article> mArticleList;
    ACProgressFlower mDialog;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.card_article_title);
            thumbnail = (ImageView) view.findViewById(R.id.article_thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }

    public ArticleAdapter(Context context, List<Article> articleList) {
        this.mContext = context;
        this.mArticleList = articleList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Article article = mArticleList.get(position);
        holder.title.setText(article.getTitle());
        Glide.with(mContext).load(article.getImage()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, position);
            }
        });
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_article_card, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popupMenu.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private ArticlesDataSource dataSource;
        private int position;
        String id;
        String title;
        String body;
        String source;
        String image;
        String imageName = "";

        public MyMenuItemClickListener(int position) {
            this.position = position;
            id = mArticleList.get(this.position).getId();
            title = mArticleList.get(this.position).getTitle();
            image = mArticleList.get(this.position).getImage();

            mDialog = new ACProgressFlower.Builder(mContext)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .fadeColor(Color.DKGRAY).build();
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    mDialog.show();
                    loadArticle();
                    return true;
                case R.id.action_delete_article:
                    Toast.makeText(mContext, "Article Deleted", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }

        public void loadArticle() {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String token = sharedPreferences.getString("user_token", "");
            String url = "https://nameless-lowlands-50285.herokuapp.com/api/article/" + id;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "onResponse: " + response.toString());
                    try {
                        if (response.getBoolean("success")) {
                            body = response.getString("content");
                            source = response.getString("source");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Glide.with(mContext.getApplicationContext())
                            .load(image)
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                                    String name = new Date().toString() + ".jpg";
                                    Log.d(TAG, "onResourceReady: name = " + name);
                                    imageName = imageName + name.replaceAll("\\s+", "");
                                    Log.d(TAG, "onResourceReady: imageName = " + imageName);
                                    ContextWrapper contextWrapper = new ContextWrapper(mContext);
                                    File directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);

                                    File myPath = new File(directory, imageName);

                                    dataSource = new ArticlesDataSource(mContext);
                                    Log.d(TAG, "onResponse: id: " + id);
                                    dataSource.createArticle(id, title, body, source, imageName);
                                    mDialog.dismiss();
                                    Toast.makeText(mContext, "Added to favourite", Toast.LENGTH_SHORT).show();

                                    FileOutputStream fileOutputStream = null;
                                    try {
                                        fileOutputStream = new FileOutputStream(myPath);
                                        resource.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        try {
                                            fileOutputStream.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
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

            MySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
        }
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }

}
