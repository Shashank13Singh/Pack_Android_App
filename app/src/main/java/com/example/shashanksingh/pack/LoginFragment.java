package com.example.shashanksingh.pack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

/**
 * Created by Shashank Singh on 3/28/2017.
 */

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    String mToken = "user_token";
    
    EditText mUsername, mPassword;
    Button mLogin;
    final String url = "https://nameless-lowlands-50285.herokuapp.com/api/article/login/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_login, container, false);

        mUsername = (EditText) rootView.findViewById(R.id.login_name_edit_text);
        mPassword = (EditText) rootView.findViewById(R.id.login_password_edit_text);
        mLogin = (Button) rootView.findViewById(R.id.login_button);

        final ACProgressFlower dialog = new ACProgressFlower.Builder(getContext())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.DKGRAY).build();


        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.show();

                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("username", mUsername.getText().toString());
                stringMap.put("password", mPassword.getText().toString());

                Uri.Builder builder = new Uri.Builder();
                Iterator entries = stringMap.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry) entries.next();
                    builder.appendQueryParameter(entry.getKey().toString(), entry.getValue().toString());
                    entries.remove();
                }
                final String requestBody = builder.build().getEncodedQuery();

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: " + response.toString());
                        try {
                            if (response.getBoolean("success")) {
                                sessionManager(response.getString("token"));
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.putExtra("Username", response.getString("username"));
                                intent.putExtra("Token", response.getString("token"));
                                intent.putExtra("Email", response.getString("email"));
                                intent.putExtra("Name", response.getString("name"));
                                startActivity(intent);
                                getActivity().finish();
                                dialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Log.i("onErrorResponse", error.toString());
                        Toast.makeText(getActivity().getApplicationContext(), "Invalid Username or Password", Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded";
                    }
                };

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
            }
        });

        return rootView;
    }

    public void sessionManager(String token) {
        SharedPreferences pref = this.getActivity().getSharedPreferences("PackPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.putString(mToken, token).apply();
    }
}
