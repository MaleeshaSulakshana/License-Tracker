package com.licencetracker.licencetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.licencetracker.licencetracker.User.PswChangeActivity;
import com.licencetracker.licencetracker.User.UserDashboardActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class RegistrationActivity extends AppCompatActivity {

    private TextView linkSignUp;
    private Button btnSignUp;

    private EditText fname, lname, nic, number, email, psw, cpsw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        fname = this.findViewById(R.id.fname);
        lname = this.findViewById(R.id.lname);
        nic = this.findViewById(R.id.nic);
        number = this.findViewById(R.id.number);
        email = this.findViewById(R.id.email);
        psw = this.findViewById(R.id.psw);
        cpsw = this.findViewById(R.id.cpsw);

        linkSignUp = this.findViewById(R.id.linkSignUp);
        btnSignUp = this.findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signUp();

            }
        });


        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

    }

    private void signUp() {

        String fnameValue = fname.getText().toString();
        String lnameValue = lname.getText().toString();
        String nicValue = nic.getText().toString();
        String numberValue = number.getText().toString();
        String emailValue = email.getText().toString();
        String pswValue = psw.getText().toString();
        String cpswValue = cpsw.getText().toString();

        if (fnameValue.equals("") || lnameValue.equals("") || nicValue.equals("") || numberValue.equals("") ||
                emailValue.equals("") || pswValue.equals("") || cpswValue.equals("") ) {
            Toast.makeText(RegistrationActivity.this, "Fields empty!",Toast.LENGTH_SHORT).show();

        } else if (!pswValue.equals(cpswValue)) {
            Toast.makeText(RegistrationActivity.this, "Password and confirm password not matched!",Toast.LENGTH_SHORT).show();

        } else {

            try {
                String URL = API.DRIVER_API + "register";

                RequestQueue requestQueue = Volley.newRequestQueue(RegistrationActivity.this);
                JSONObject jsonBody = new JSONObject();

                jsonBody.put("first_name", fnameValue);
                jsonBody.put("last_name", lnameValue);
                jsonBody.put("nic", nicValue);
                jsonBody.put("mobile", numberValue);
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
                                fname.setText("");
                                lname.setText("");
                                nic.setText("");
                                number.setText("");
                                email.setText("");
                                psw.setText("");
                                cpsw.setText("");
                            }

                            Toast.makeText(RegistrationActivity.this, msg, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(RegistrationActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
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