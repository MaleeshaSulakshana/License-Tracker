package com.licencetracker.licencetracker.Policeman;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.licencetracker.licencetracker.R;
import com.licencetracker.licencetracker.User.UserProfileActivity;
import com.licencetracker.licencetracker.User.UserUploadImagesActivity;

import java.io.IOException;

public class PolicemanQrScanActivity extends AppCompatActivity {

    Button btnScanQr;
    private Dialog qrDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policeman_qr_scan);

        btnScanQr = this.findViewById(R.id.btnScanQr);
        btnScanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanner();

            }
        });

    }


    public void scanner() {
        IntentIntegrator integrator = new IntentIntegrator(PolicemanQrScanActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("");
        integrator.setCameraId(0);
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result =  IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(PolicemanQrScanActivity.this, "Qr scan canceled", Toast.LENGTH_SHORT).show();
            } else {
                String qrValue = result.getContents().toString();

//                Toast.makeText(this, qrValue, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(PolicemanQrScanActivity.this, PolicemanViewDriverDetailsActivity.class);
                intent.putExtra("driverId", qrValue);
                startActivity(intent);
                finish();

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}