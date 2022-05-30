package com.licencetracker.licencetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.licencetracker.licencetracker.Classes.Preferences;
import com.licencetracker.licencetracker.Policeman.PolicemanDashboardActivity;
import com.licencetracker.licencetracker.User.UserDashboardActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnStart;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean doubleBackToExitPressedOnce = false;
    private Dialog exitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Get layouts
        exitDialog = new Dialog(MainActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        btnStart = this.findViewById(R.id.btnStart);

//        For shared preferences
        sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = sharedPreferences.getString("id", "");
                String user_type = sharedPreferences.getString("user_type", "");

                if (!id.equals("") && !user_type.equals("")) {

                    if (user_type.equals("driver")) {

                        Preferences.LOGGED_USER_ID = id;
                        Preferences.LOGGED_USER_TYPE = user_type;

                        Intent intent = new Intent(MainActivity.this, UserDashboardActivity.class);
                        startActivity(intent);
                        finish();

                    } else if (user_type.equals("policeman")) {

                        Preferences.LOGGED_USER_ID = id;
                        Preferences.LOGGED_USER_TYPE = user_type;

                        Intent intent = new Intent(MainActivity.this, PolicemanDashboardActivity.class);
                        startActivity(intent);
                        finish();

                    } else {

                        Preferences.LOGGED_USER_ID = "";
                        Preferences.LOGGED_USER_TYPE = "";

                        editor.clear();
                        editor.apply();

                        Intent intent = new Intent(MainActivity.this, UserTypeSelectionActivity.class);
                        startActivity(intent);
                        finish();

                    }

                } else {

                    Preferences.LOGGED_USER_ID = "";
                    Preferences.LOGGED_USER_TYPE = "";

                    Intent intent = new Intent(MainActivity.this, UserTypeSelectionActivity.class);
                    startActivity(intent);
                    finish();

                }

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