package com.licencetracker.licencetracker.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.licencetracker.licencetracker.Classes.Preferences;
import com.licencetracker.licencetracker.R;

public class UserQrActivity extends AppCompatActivity {

    private ImageView qrCode;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_qr);

        qrCode = this.findViewById(R.id.qrCode);
        btnBack = this.findViewById(R.id.btnBack);

        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = writer.encode(Preferences.LOGGED_USER_ID.toString(), BarcodeFormat.QR_CODE, 300, 300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            qrCode.setImageBitmap(bitmap);

        } catch (WriterException e)
        {
            Toast.makeText(UserQrActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UserQrActivity.this, UserDashboardActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }
}