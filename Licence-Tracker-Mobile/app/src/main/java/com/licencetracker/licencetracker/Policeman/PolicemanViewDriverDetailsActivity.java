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
import com.licencetracker.licencetracker.User.ImageViewActivity;
import com.licencetracker.licencetracker.User.UserDashboardActivity;
import com.licencetracker.licencetracker.User.UserProfileActivity;

import org.json.JSONArray;
import org.json.JSONException;

public class PolicemanViewDriverDetailsActivity extends AppCompatActivity {

    private EditText fname, lname, nic, number, email;
    private Button btnViewNic, btnViewLicence, btnViewPassport, btnAddFine, btnViewFineList;

    String driverId = "", imageNic="", imagePassport="", imageLicence = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policeman_view_driver_details);

        Intent project = getIntent();
        driverId = project.getStringExtra("driverId");

        fname = this.findViewById(R.id.fname);
        lname = this.findViewById(R.id.lname);
        nic = this.findViewById(R.id.nic);
        number = this.findViewById(R.id.number);
        email = this.findViewById(R.id.email);

        btnViewNic = this.findViewById(R.id.btnViewNic);
        btnViewLicence = this.findViewById(R.id.btnViewLicence);
        btnViewPassport = this.findViewById(R.id.btnViewPassport);
        btnAddFine = this.findViewById(R.id.btnAddFine);
        btnViewFineList = this.findViewById(R.id.btnViewFineList);

        getDriverDetails();

        btnViewNic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = API.IMAGE_API + "drivers/nic/" + imageNic;

                Intent intent = new Intent(PolicemanViewDriverDetailsActivity.this, PolicemanViewImageActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);

            }
        });

        btnViewLicence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = API.IMAGE_API + "drivers/license/" + imageLicence;

                Intent intent = new Intent(PolicemanViewDriverDetailsActivity.this, PolicemanViewImageActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);

            }
        });

        btnViewPassport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = API.IMAGE_API + "drivers/passport/" + imagePassport;

                Intent intent = new Intent(PolicemanViewDriverDetailsActivity.this, PolicemanViewImageActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);

            }
        });

        btnAddFine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PolicemanViewDriverDetailsActivity.this, PolicemanAddFineActivity.class);
                intent.putExtra("driverId", driverId);
                startActivity(intent);

            }
        });

        btnViewFineList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PolicemanViewDriverDetailsActivity.this, PolicemanViewFineListActivity.class);
                intent.putExtra("driverId", driverId);
                startActivity(intent);

            }
        });

    }

    private void getDriverDetails() {

        String URL = API.DRIVER_API + driverId;

        RequestQueue requestQueue = Volley.newRequestQueue(PolicemanViewDriverDetailsActivity.this);
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

                                imageNic = (String) responseData.get(7);
                                imageLicence = (String) responseData.get(8);
                                imagePassport = (String) responseData.get(9);

                                if (imageNic.equals("")) {
                                    btnViewNic.setVisibility(View.GONE);
                                }

                                if (imageLicence.equals("")) {
                                    btnViewLicence.setVisibility(View.GONE);
                                }

                                if (imagePassport.equals("")) {
                                    btnViewPassport.setVisibility(View.GONE);
                                }


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
                        Toast.makeText(PolicemanViewDriverDetailsActivity.this, error.toString(),Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(PolicemanViewDriverDetailsActivity.this, PolicemanQrScanActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }
}