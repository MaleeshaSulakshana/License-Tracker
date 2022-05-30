package com.licencetracker.licencetracker.Policeman;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.licencetracker.licencetracker.API.API;
import com.licencetracker.licencetracker.Classes.Preferences;
import com.licencetracker.licencetracker.R;
import com.licencetracker.licencetracker.User.UserProfileActivity;

import org.json.JSONArray;
import org.json.JSONException;

public class PolicemanProfileActivity extends AppCompatActivity {

    private EditText policeId, name, email, number, designation;
    private Button btnGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policeman_profile);

        policeId = this.findViewById(R.id.policeId);
        name = this.findViewById(R.id.name);
        email = this.findViewById(R.id.email);
        number = this.findViewById(R.id.number);
        designation = this.findViewById(R.id.designation);

        btnGoBack = this.findViewById(R.id.btnGoBack);

        getProfileData();

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PolicemanProfileActivity.this, PolicemanDashboardActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }

    private void getProfileData() {

        String URL = API.POLICEMAN_API + Preferences.LOGGED_USER_ID;

        RequestQueue requestQueue = Volley.newRequestQueue(PolicemanProfileActivity.this);
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

                                String idValue = (String) responseData.get(0).toString();
                                String policeIdValue = (String) responseData.get(1).toString();
                                String nameValue = (String) responseData.get(2).toString();
                                String emailValue = (String) responseData.get(3).toString();
                                String mobileValue = (String) responseData.get(5).toString();
                                String designationValue = (String) responseData.get(6).toString();

                                policeId.setText(policeIdValue);
                                name.setText(nameValue);
                                email.setText(emailValue);
                                number.setText(mobileValue);
                                designation.setText(designationValue);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PolicemanProfileActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }

}