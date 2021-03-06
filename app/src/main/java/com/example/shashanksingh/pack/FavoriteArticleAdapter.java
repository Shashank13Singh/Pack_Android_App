package com.example.shashanksingh.pack;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

/**
 * Created by Shashank Singh on 4/20/2017.
 */

public class FavoriteArticleAdapter extends RecyclerView.Adapter<FavoriteArticleAdapter.MyViewHolder> {
    private Context mContext;
    private List<Article> mArticleList;
    ACProgressFlower mDialog;
    private static final String TAG = "FavoriteArticleAdapter";

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

    public FavoriteArticleAdapter(Context context, List<Article> articleList) {
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
        String imageName = article.getImage();
        ContextWrapper contextWrapper = new ContextWrapper(mContext);
        File directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);
        String path = directory.getAbsolutePath();
        path = path + "/" + imageName;
        Glide.with(mContext).load(path).into(holder.thumbnail);

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
        inflater.inflate(R.menu.menu_article_card_fav, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popupMenu.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int position;
        private ArticlesDataSource mDataSource;

        public MyMenuItemClickListener(int position) {
            this.position = position;
            mDialog = new ACProgressFlower.Builder(mContext)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .fadeColor(Color.DKGRAY).build();
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_remove_favourite:
                    removeArticle();
                    Toast.makeText(mContext, "Article removed from favourite", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }

        public void removeArticle() {
            mDataSource = new ArticlesDataSource(mContext);
            Article article = mArticleList.get(position);
            mDataSource.deleteArticle(article);
            mArticleList.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }

}
