package com.licencetracker.licencetracker.User;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.licencetracker.licencetracker.Classes.Preferences;
import com.licencetracker.licencetracker.MainActivity;
import com.licencetracker.licencetracker.Policeman.PolicemanDashboardActivity;
import com.licencetracker.licencetracker.R;

public class UserDashboardActivity extends AppCompatActivity {

    private LinearLayout btnQr, btnProfile, btnPswChange, btnLogout, btnImageShow, btnFine;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean doubleBackToExitPressedOnce = false;
    private Dialog exitDialog, typeSelectDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

//        Get layouts
        exitDialog = new Dialog(UserDashboardActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        typeSelectDialog = new Dialog(UserDashboardActivity.this);
        typeSelectDialog.setContentView(R.layout.image_type_dialog_box);

        btnQr = this.findViewById(R.id.btnQr);
        btnProfile = this.findViewById(R.id.btnProfile);
        btnPswChange = this.findViewById(R.id.btnPswChange);
        btnLogout = this.findViewById(R.id.btnLogout);
        btnImageShow = this.findViewById(R.id.btnImageShow);
        btnFine = this.findViewById(R.id.btnFine);


//        For shared preferences
        sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        btnQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UserDashboardActivity.this, UserQrActivity.class);
                startActivity(intent);

            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UserDashboardActivity.this, UserProfileActivity.class);
                startActivity(intent);

            }
        });

        btnPswChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UserDashboardActivity.this, PswChangeActivity.class);
                startActivity(intent);

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Preferences.LOGGED_USER_ID = "";
                Preferences.LOGGED_USER_TYPE = "";

                editor.clear();
                editor.apply();

                Intent intent = new Intent(UserDashboardActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();

            }
        });

        btnImageShow.setOnClickListener(new View.OnClickListener() {
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
                        Intent intent = new Intent(UserDashboardActivity.this, ImageViewActivity.class);
                        intent.putExtra("type", "nic");
                        startActivity(intent);
                        finish();
                    }
                });

                btnLicense.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        typeSelectDialog.dismiss();
                        Intent intent = new Intent(UserDashboardActivity.this, ImageViewActivity.class);
                        intent.putExtra("type", "license");
                        startActivity(intent);
                        finish();
                    }
                });

                btnPassport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        typeSelectDialog.dismiss();
                        Intent intent = new Intent(UserDashboardActivity.this, ImageViewActivity.class);
                        intent.putExtra("type", "passport");
                        startActivity(intent);
                        finish();
                    }
                });

            }
        });


        btnFine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UserDashboardActivity.this, UserFinesListActivity.class);
                startActivity(intent);

            }
        });


    }


    //    Tap to close app
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        Button btnExitYes, btnExitNo;
        exitDialog.show();

        btnExitYes = (Button) exitDialog.findViewById(R.id.btnYes);
        btnExitYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

        btnExitNo = (Button) exitDialog.findViewById(R.id.btnNo);
        btnExitNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });

    }


}