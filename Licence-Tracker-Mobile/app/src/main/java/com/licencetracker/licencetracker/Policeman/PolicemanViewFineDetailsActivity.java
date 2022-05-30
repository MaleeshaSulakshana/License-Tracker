package com.licencetracker.licencetracker.Policeman;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.licencetracker.licencetracker.User.UserFineDetailsActivity;

import org.json.JSONArray;
import org.json.JSONException;

public class PolicemanViewFineDetailsActivity extends AppCompatActivity {

    private EditText date, vehicle, location, offence, amount, oname, odesignation;

    private String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policeman_view_fine_details);

        Intent project = getIntent();
        id = project.getStringExtra("id");

        date = this.findViewById(R.id.date);
        vehicle = this.findViewById(R.id.vehicle);
        location = this.findViewById(R.id.location);
        offence = this.findViewById(R.id.offence);
        amount = this.findViewById(R.id.amount);
        oname = this.findViewById(R.id.oname);
        odesignation = this.findViewById(R.id.odesignation);

        getFineDetails();

    }


    private void getFineDetails() {

        String URL = API.FINE_API + id;
        System.out.println("********* " + URL);

        RequestQueue requestQueue = Volley.newRequestQueue(PolicemanViewFineDetailsActivity.this);
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

                                String dateValue = (String) responseData.get(3).toString();
                                String vehicleValue = (String) responseData.get(7).toString();
                                String locationValue = (String) responseData.get(6).toString();
                                String offenceValue = (String) responseData.get(4).toString();
                                String amountValue = (String) responseData.get(5).toString();
                                String onameValue = (String) responseData.get(21).toString();
                                String odesignationValue = (String) responseData.get(25).toString();

                                date.setText(dateValue);
                                vehicle.setText(vehicleValue);
                                location.setText(locationValue);
                                offence.setText(offenceValue);
                                amount.setText(amountValue);
                                oname.setText(onameValue);
                                odesignation.setText(odesignationValue);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PolicemanViewFineDetailsActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }


}