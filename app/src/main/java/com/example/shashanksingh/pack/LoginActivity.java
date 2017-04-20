package com.example.shashanksingh.pack;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;

public class LoginActivity extends FragmentActivity implements AppCompatCallback {

    public static final String MyPREFERENCES = "MyPrefs";
    private static final String TAG = "LoginActivity";
    private static int NUM_PAGES = 2;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private AppCompatDelegate mDelegate;
    private TabLayout mFragmentTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDelegate = AppCompatDelegate.create(this, this);
        mDelegate.onCreate(savedInstanceState);
        mDelegate.setContentView(R.layout.activity_login);

//        SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean("isLoggedIn", true);
//        editor.commit();

        mPager = (ViewPager) findViewById(R.id.login_signUp_pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mFragmentTabs = (TabLayout) findViewById(R.id.login_signUp);
        mFragmentTabs.setupWithViewPager(mPager);

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private String[] tabTitles = new String[]{"Sign in", "Sign Up"};

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
                    return new LoginFragment();
                case 1:
                    return new SignUpFragment();
                default:
                    return new LoginFragment();
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
}
