package com.licencetracker.licencetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.licencetracker.licencetracker.API.API;
import com.licencetracker.licencetracker.Classes.Preferences;
import com.licencetracker.licencetracker.Policeman.PolicemanDashboardActivity;
import com.licencetracker.licencetracker.User.UserDashboardActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LoginActivity extends AppCompatActivity {

    private Button btnSignIn;
    private TextView linkSignUp;
    private EditText email, psw;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent project = getIntent();
        type = project.getStringExtra("type");

        btnSignIn = this.findViewById(R.id.btnSignIn);
        linkSignUp = this.findViewById(R.id.linkSignUp);

        email = this.findViewById(R.id.email);
        psw = this.findViewById(R.id.psw);

//        For shared preferences
        sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login();

            }
        });


        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);

            }
        });

    }

    private void login() {

        String emailValue = email.getText().toString();
        String pswValue = psw.getText().toString();

        if (emailValue.equals("") || pswValue.equals("")) {
            Toast.makeText(LoginActivity.this, "Fields empty!",Toast.LENGTH_SHORT).show();

        } else {

            try {
                String URL = "";

                if (type.equals("driver")) {
                    URL = API.DRIVER_API + "login";

                } else if (type.equals("policeman")) {
                    URL = API.POLICEMAN_API + "login";
                }

                RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                JSONObject jsonBody = new JSONObject();

                jsonBody.put("email", emailValue);
                jsonBody.put("psw", pswValue);

                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String status = jsonObject.getString("status");
                            String msg = jsonObject.getString("msg");

                            if (status.equals("success")) {

                                email.setText("");
                                psw.setText("");

                                String id = jsonObject.getString("id");

                                Preferences.LOGGED_USER_ID = id;
                                Preferences.LOGGED_USER_TYPE = type;

                                if (type.equals("driver")) {

                                    Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                                    startActivity(intent);
                                    finish();

                                    editor.putString("id", id );
                                    editor.putString("user_type", type );
                                    editor.commit();

                                } else if (type.equals("policeman")) {

                                    Intent intent = new Intent(LoginActivity.this, PolicemanDashboardActivity.class);
                                    startActivity(intent);
                                    finish();

                                    editor.putString("id", id );
                                    editor.putString("user_type", type );
                                    editor.commit();

                                } else {
                                    Toast.makeText(LoginActivity.this, "Some error occur!.", Toast.LENGTH_SHORT).show();
                                }

                            }

                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(LoginActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return requestBody == null ? null : requestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            return null;
                        }
                    }
                };

                requestQueue.add(stringRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}