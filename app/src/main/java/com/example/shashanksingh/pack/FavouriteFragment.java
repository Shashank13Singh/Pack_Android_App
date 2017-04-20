package com.example.shashanksingh.pack;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shashank Singh on 3/6/2017.
 */

public class FavouriteFragment extends Fragment {
    AVLoadingIndicatorView mAviLib;
    private List<Article> mArticles;
    private FavoriteArticleAdapter mArticleAdapter;
    private RecyclerView mRecyclerView;
    private static final String TAG = "FavouriteFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_favourite, container, false);

        mAviLib = (AVLoadingIndicatorView) rootView.findViewById(R.id.avi_fav);
        mAviLib.show();

        mArticles = new ArrayList<>();

        loadArticleList();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        mArticleAdapter = new FavoriteArticleAdapter(getContext(), mArticles);
        mRecyclerView.setAdapter(mArticleAdapter);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getActivity(), DetailArticleActivity.class);
                        intent.putExtra("id", mArticles.get(position).getId());
                        startActivity(intent);
                    }
                }));

        return rootView;
    }
    public void loadArticleList() {

        ArticlesDataSource dataSource = new ArticlesDataSource(getContext());
        mArticles = dataSource.getAllArticles();
        mAviLib.hide();
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
