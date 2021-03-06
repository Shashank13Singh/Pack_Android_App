package com.example.shashanksingh.pack;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

/**
 * Created by Shashank Singh on 3/28/2017.
 */

public class SignUpFragment extends Fragment {

    EditText mFirstName, mLastName, mUsername, mEmail, mPassword;
    Button mSignUpButton;
    ACProgressFlower dialog;
    final String url = "https://nameless-lowlands-50285.herokuapp.com/api/article/register/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_signup, container, false);

        mFirstName = (EditText) rootView.findViewById(R.id.user_first_name_edit_text);
        mLastName = (EditText) rootView.findViewById(R.id.user_last_name_edit_text);
        mUsername = (EditText) rootView.findViewById(R.id.user_name_edit_text);
        mPassword = (EditText) rootView.findViewById(R.id.signUp_password_edit_text);
        mEmail = (EditText) rootView.findViewById(R.id.signUp_email_edit_text);
        mSignUpButton = (Button) rootView.findViewById(R.id.signup_button);

        mFirstName.setSingleLine();
        mLastName.setSingleLine();
        mUsername.setSingleLine();
        mEmail.setSingleLine();

        mPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_DONE) {
                    signup();
                    handled = true;
                }
                return handled;
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFirstName !=null && mLastName != null && mPassword != null && mUsername != null && mEmail != null) {
                    dialog = new ACProgressFlower.Builder(getContext())
                            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                            .themeColor(Color.WHITE)
                            .fadeColor(Color.DKGRAY).build();
                    signup();
                }
                else {
                    Toast.makeText(getContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return rootView;
    }

    public void signup() {
        dialog.show();

        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("first_name", mFirstName.getText().toString());
        stringMap.put("last_name", mLastName.getText().toString());
        stringMap.put("username", mUsername.getText().toString());
        stringMap.put("password", mPassword.getText().toString());
        stringMap.put("email", mEmail.getText().toString());

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
                dialog.dismiss();
                Toast.makeText(getContext(), "Sign Up Successful, Please Login", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.i("onErrorResponse", error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
}
