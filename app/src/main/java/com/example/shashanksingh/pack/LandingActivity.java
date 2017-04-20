package com.example.shashanksingh.pack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class LandingActivity extends AppCompatActivity {

    private static final String TAG = "LandingActivity";
    public static final String MyPREFERENCES = "PackPref";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final String token = sharedPreferences.getString("user_token", "");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: " + token);
                if (!(token.length() > 0)) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();
            }
        }, 3000);
    }
}
