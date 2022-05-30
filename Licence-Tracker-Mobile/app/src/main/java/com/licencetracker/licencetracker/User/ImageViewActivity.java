package com.licencetracker.licencetracker.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

public class ImageViewActivity extends AppCompatActivity {

    private ImageView imageView;

    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        Intent project = getIntent();
        type = project.getStringExtra("type");

        imageView = this.findViewById(R.id.imageView);

        getProfileData();

    }

    //    Tap to go back
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(ImageViewActivity.this, UserDashboardActivity.class);
        startActivity(intent);
        finish();

    }

    private void getProfileData() {

        String URL = API.DRIVER_API + Preferences.LOGGED_USER_ID;

        RequestQueue requestQueue = Volley.newRequestQueue(ImageViewActivity.this);
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

                                String imageNic = (String) responseData.get(7);
                                String imageLicence = (String) responseData.get(8);
                                String imagePassport = (String) responseData.get(9);

                                String imageValue= "";

                                if (type.equals("nic")) {
                                    imageValue = API.IMAGE_API + "drivers/nic/" + imageNic;
                                } else if (type.equals("passport")) {
                                    imageValue = API.IMAGE_API + "drivers/passport/" + imagePassport;
                                } else if (type.equals("license")) {
                                    imageValue = API.IMAGE_API + "drivers/license/" + imageLicence;
                                }

                                if (imageValue.equals("")) {

                                    Toast.makeText(ImageViewActivity.this, "Image not uploaded", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(ImageViewActivity.this, UserDashboardActivity.class);
                                    startActivity(intent);
                                    finish();

                                }

                                Uri imgUri = Uri.parse(imageValue);
                                Picasso.get().load(imgUri).into(imageView);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ImageViewActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }

}