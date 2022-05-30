package com.licencetracker.licencetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserTypeSelectionActivity extends AppCompatActivity {

    private Button btnPoliceman, btnDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type_selection);

        btnPoliceman = this.findViewById(R.id.btnPoliceman);
        btnDriver = this.findViewById(R.id.btnDriver);

        btnPoliceman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UserTypeSelectionActivity.this, LoginActivity.class);
                intent.putExtra("type", "policeman");
                startActivity(intent);
                finish();

            }
        });

        btnDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UserTypeSelectionActivity.this, LoginActivity.class);
                intent.putExtra("type", "driver");
                startActivity(intent);
                finish();

            }
        });

    }
}