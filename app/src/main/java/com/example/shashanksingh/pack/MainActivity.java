package com.example.shashanksingh.pack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.graphics.Palette;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

import static com.example.shashanksingh.pack.LandingActivity.MyPREFERENCES;

public class MainActivity extends FragmentActivity implements AppCompatCallback {

    private static int NUM_PAGES = 3;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private AppCompatDelegate mDelegate;
    private TabLayout mFragmentTabs;
    CollapsingToolbarLayout mCollapsingToolbar;
    ACProgressFlower dialog;

    private static final String TAG = "MainActivity";
    String url = "https://nameless-lowlands-50285.herokuapp.com/api/article/logout/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDelegate = AppCompatDelegate.create(this, this);
        mDelegate.onCreate(savedInstanceState);
        mDelegate.setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDelegate.setSupportActionBar(toolbar);

        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.DKGRAY).build();

        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.main_toolbar);

        initCollapsingToolbar();

        final Window window = this.getWindow();

        try {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.header);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @SuppressWarnings("ResourceType")
                @Override
                public void onGenerated(Palette palette) {

                    int vibrantColor = palette.getVibrantColor(R.color.primary_500);
                    int vibrantDarkColor = palette.getDarkVibrantColor(R.color.primary_700);
                    mCollapsingToolbar.setContentScrimColor(vibrantColor);
                    mCollapsingToolbar.setStatusBarScrimColor(vibrantDarkColor);

                    if (android.os.Build.VERSION.SDK_INT >= 21) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        window.setStatusBarColor(vibrantDarkColor);
                    }
                }
            });

        } catch (Exception e) {
            // if Bitmap fetch fails, fallback to primary colors
            Log.e(TAG, "onCreate: failed to create bitmap from background", e.fillInStackTrace());
            mCollapsingToolbar.setContentScrimColor(
                    ContextCompat.getColor(this, R.color.primary_500)
            );
            mCollapsingToolbar.setStatusBarScrimColor(
                    ContextCompat.getColor(this, R.color.primary_700)
            );
        }

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mFragmentTabs = (TabLayout) findViewById(R.id.fragment_tabs);
        mFragmentTabs.setupWithViewPager(mPager);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private String[] tabTitles = new String[]{"Library", "Favourite", "Recommended"};

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new LibraryFragment();
                case 1:
                    return new FavouriteFragment();
                case 2:
                    return new RecommendedFragment();
                default:
                    return new LibraryFragment();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {

    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {

    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }

    private void initCollapsingToolbar() {
        mCollapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBar_layout);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mCollapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    mCollapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            case R.id.exit:
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void logout() {
        dialog.show();

        SharedPreferences sharedPreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final String token = sharedPreferences.getString("user_token", "");
        sharedPreferences.edit().remove("user_token").apply();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: " + response.toString());
                try {
                    if (response.getBoolean("success")) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
