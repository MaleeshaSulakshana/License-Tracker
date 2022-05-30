package com.licencetracker.licencetracker.User;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.licencetracker.licencetracker.API.API;
import com.licencetracker.licencetracker.Classes.Preferences;
import com.licencetracker.licencetracker.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class UserProfileActivity extends AppCompatActivity {

    private EditText fname, lname, nic, number, email;
    private Button btnUpdate, btnUpload;

    private Dialog typeSelectDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

//        Get layouts
        typeSelectDialog = new Dialog(UserProfileActivity.this);
        typeSelectDialog.setContentView(R.layout.image_type_dialog_box);

        fname = this.findViewById(R.id.fname);
        lname = this.findViewById(R.id.lname);
        nic = this.findViewById(R.id.nic);
        number = this.findViewById(R.id.number);
        email = this.findViewById(R.id.email);

        btnUpdate = this.findViewById(R.id.btnUpdate);
        btnUpload = this.findViewById(R.id.btnUpload);

        getProfileData();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateProfile();

            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Button btnNic, btnLicense, btnPassport;
                typeSelectDialog.show();

                btnNic = (Button) typeSelectDialog.findViewById(R.id.btnNic);
                btnLicense = (Button) typeSelectDialog.findViewById(R.id.btnLicense);
                btnPassport = (Button) typeSelectDialog.findViewById(R.id.btnPassport);

                btnNic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        typeSelectDialog.dismiss();
                        Intent intent = new Intent(UserProfileActivity.this, UserUploadImagesActivity.class);
                        intent.putExtra("type", "nic");
                        startActivity(intent);
                        finish();
                    }
                });

                btnLicense.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        typeSelectDialog.dismiss();
                        Intent intent = new Intent(UserProfileActivity.this, UserUploadImagesActivity.class);
                        intent.putExtra("type", "license");
                        startActivity(intent);
                        finish();
                    }
                });

                btnPassport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        typeSelectDialog.dismiss();
                        Intent intent = new Intent(UserProfileActivity.this, UserUploadImagesActivity.class);
                        intent.putExtra("type", "passport");
                        startActivity(intent);
                        finish();
                    }
                });

            }
        });

    }

    private void getProfileData() {

        String URL = API.DRIVER_API + Preferences.LOGGED_USER_ID;

        RequestQueue requestQueue = Volley.newRequestQueue(UserProfileActivity.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {

                            if (response.length() > 0) {

                                JSONArray responseData = response.getJSONArray(0);

                                Integer id = (Integer) responseData.get(0);
                                String firstName = (String) responseData.get(1);
                                String lastName = (String) responseData.get(2);
                                String NIC = (String) responseData.get(3);
                                String Number = (String) responseData.get(4);
                                String Email = (String) responseData.get(5);

                                fname.setText(firstName);
                                lname.setText(lastName);
                                nic.setText(NIC);
                                number.setText(Number);
                                email.setText(Email);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserProfileActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }

    private void updateProfile() {

        String idValue = Preferences.LOGGED_USER_ID;
        String fnameValue = fname.getText().toString();
        String lnameValue = lname.getText().toString();
        String nicValue = nic.getText().toString();
        String numberValue = number.getText().toString();

        if (fnameValue.equals("") || lnameValue.equals("") || nicValue.equals("") || numberValue.equals("")) {

            Toast.makeText(UserProfileActivity.this, "Text boxes are empty", Toast.LENGTH_SHORT).show();

        } else if (numberValue.length() != 10 ) {

            Toast.makeText(UserProfileActivity.this, "Mobile number invalid", Toast.LENGTH_SHORT).show();

        } else {

            try {
                String URL = API.DRIVER_API + "profile_update";

                RequestQueue requestQueue = Volley.newRequestQueue(UserProfileActivity.this);
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("driver", idValue);
                jsonBody.put("first_name", fnameValue);
                jsonBody.put("last_name", lnameValue);
                jsonBody.put("nic", nicValue);
                jsonBody.put("mobile", numberValue);

                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String status = jsonObject.getString("status");
                            String msg = jsonObject.getString("msg");

                            Toast.makeText(UserProfileActivity.this, msg, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(UserProfileActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
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