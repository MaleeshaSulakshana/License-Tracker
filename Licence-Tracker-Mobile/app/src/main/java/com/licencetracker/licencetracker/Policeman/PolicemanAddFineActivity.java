package com.licencetracker.licencetracker.Policeman;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.licencetracker.licencetracker.API.API;
import com.licencetracker.licencetracker.Classes.Preferences;
import com.licencetracker.licencetracker.R;
import com.licencetracker.licencetracker.User.UserProfileActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class PolicemanAddFineActivity extends AppCompatActivity {

    private EditText offence, amount, location, vehicle;
    private Button btnAdd;

    private String driverId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policeman_add_fine);

        Intent project = getIntent();
        driverId = project.getStringExtra("driverId");

        offence = this.findViewById(R.id.offence);
        amount = this.findViewById(R.id.amount);
        location = this.findViewById(R.id.location);
        vehicle = this.findViewById(R.id.vehicle);

        btnAdd = this.findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addFine();

            }
        });

    }

    private void addFine() {

        String policemanIdValue = Preferences.LOGGED_USER_ID;
        String offenceValue = offence.getText().toString();
        String amountValue = amount.getText().toString();
        String locationValue = location.getText().toString();
        String vehicleValue = vehicle.getText().toString();

        if (offenceValue.equals("") || amountValue.equals("") || locationValue.equals("") || vehicleValue.equals("")) {

            Toast.makeText(PolicemanAddFineActivity.this, "Text boxes are empty", Toast.LENGTH_SHORT).show();

        } else {

            try {
                String URL = API.FINE_API;

                RequestQueue requestQueue = Volley.newRequestQueue(PolicemanAddFineActivity.this);
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("driver_id", driverId);
                jsonBody.put("policeman_id", policemanIdValue);
                jsonBody.put("wrong_details", offenceValue);
                jsonBody.put("amount", amountValue);
                jsonBody.put("location", locationValue);
                jsonBody.put("vehicle_no", vehicleValue);

                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String status = jsonObject.getString("status");
                            String msg = jsonObject.getString("msg");

                            Toast.makeText(PolicemanAddFineActivity.this, msg, Toast.LENGTH_SHORT).show();

                            if (status.equals("success")) {
                                Intent intent = new Intent(PolicemanAddFineActivity.this, PolicemanViewDriverDetailsActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(PolicemanAddFineActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
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