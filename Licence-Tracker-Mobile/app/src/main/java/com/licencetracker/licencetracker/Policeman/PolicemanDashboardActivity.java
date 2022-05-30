package com.licencetracker.licencetracker.Policeman;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.licencetracker.licencetracker.Classes.Preferences;
import com.licencetracker.licencetracker.MainActivity;
import com.licencetracker.licencetracker.R;
import com.licencetracker.licencetracker.User.UserDashboardActivity;

public class PolicemanDashboardActivity extends AppCompatActivity {

    private LinearLayout btnQr, btnAccount, btnPsw, btnLogout;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean doubleBackToExitPressedOnce = false;
    private Dialog exitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policeman_dashboard);

//        Get layouts
        exitDialog = new Dialog(PolicemanDashboardActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        btnQr = this.findViewById(R.id.btnQr);
        btnLogout = this.findViewById(R.id.btnLogout);
        btnAccount = this.findViewById(R.id.btnAccount);
        btnPsw = this.findViewById(R.id.btnPsw);

//        For shared preferences
        sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        btnQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PolicemanDashboardActivity.this, PolicemanQrScanActivity.class);
                startActivity(intent);

            }
        });

        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PolicemanDashboardActivity.this, PolicemanProfileActivity.class);
                startActivity(intent);

            }
        });

        btnPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PolicemanDashboardActivity.this, PolicemanPswChangeActivity.class);
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

                Intent intent = new Intent(PolicemanDashboardActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();

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