package com.licencetracker.licencetracker.Policeman;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.licencetracker.licencetracker.R;
import com.squareup.picasso.Picasso;

public class PolicemanViewImageActivity extends AppCompatActivity {

    private ImageView imageView;

    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policeman_view_image);

        Intent project = getIntent();
        url = project.getStringExtra("url");

        imageView = this.findViewById(R.id.imageView);

        Uri imgUri = Uri.parse(url);
        Picasso.get().load(imgUri).into(imageView);

    }
}