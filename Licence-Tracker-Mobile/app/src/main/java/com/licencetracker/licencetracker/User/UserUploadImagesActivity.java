package com.licencetracker.licencetracker.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.licencetracker.licencetracker.API.API;
import com.licencetracker.licencetracker.Classes.Preferences;
import com.licencetracker.licencetracker.MainActivity;
import com.licencetracker.licencetracker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class UserUploadImagesActivity extends AppCompatActivity {

    private ImageView imageView;
    private LinearLayout btnUploadLayout, btnImageCapture, btnUploadFromDevice;
    private Button btnBack;

    private static final int PICK_IMAGE = 100;
    private static final int CAMERA_PERM_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private Uri imageUri = Uri.EMPTY;
    private String isCaptured = "no";

    private Bitmap bitmap = null;

    private Dialog loadingDialog;

    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_upload_images);

        Intent project = getIntent();
        type = project.getStringExtra("type");

        loadingDialog = new Dialog(UserUploadImagesActivity.this);
        loadingDialog.setContentView(R.layout.waiting_dialog_box);

        imageView = (ImageView) this.findViewById(R.id.imageView);

        btnUploadLayout = (LinearLayout) this.findViewById(R.id.btnUploadLayout);
        btnImageCapture = (LinearLayout) this.findViewById(R.id.btnImageCapture);
        btnUploadFromDevice = (LinearLayout) this.findViewById(R.id.btnUploadFromDevice);

        btnBack = (Button) this.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UserUploadImagesActivity.this, UserProfileActivity.class);
                startActivity(intent);
                finish();

            }
        });

        btnImageCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Get permission
                getPermission();

            }
        });

        btnUploadFromDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Open camera and capture image
                openGallery();

            }
        });

        btnUploadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    uploadSelectedImage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

//    Get permission
    private void getPermission()
    {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA
            }, CAMERA_PERM_CODE);
        }
        else
        {
            openCamera();
        }

    }

//    For open gallery
    private void openGallery()
    {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

//    For open camera
    private void openCamera()
    {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to use camera", Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
//            imageView.setImageURI(imageUri);
            try {
                bitmap = (Bitmap) MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap );
            } catch (IOException e) {
                e.printStackTrace();
            }
            isCaptured = "no";

        }
        else if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST_CODE){

            Bitmap image = (Bitmap) data.getExtras().get("data");
            bitmap = image;
            imageView.setImageBitmap(image);
            imageUri = Uri.EMPTY;
            isCaptured = "yes";

        }

    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }


    private void uploadSelectedImage() throws JSONException {

        if (bitmap != null) {

            String URL = "";

            if (type.equals("nic")) {
                URL = API.DRIVER_API + "nic";
            } else if (type.equals("passport")) {
                URL = API.DRIVER_API + "passport";
            } else if (type.equals("license")) {
                URL = API.DRIVER_API + "license";
            }

            loadingDialog.show();
            loadingDialog.setCancelable(false);
            loadingDialog.setCanceledOnTouchOutside(false);

            String image = getStringImage(bitmap);
            HashMap<String,String> params =  new HashMap<>();
            params.put("image", image);
            params.put("id", Preferences.LOGGED_USER_ID);
            JSONObject parameter =  new JSONObject(params);
            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, URL, parameter, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {

                        String status = response.getString("status");
                        String msg = response.getString("msg");

                        if (status.equals("success")) {

                            bitmap = null;

                            imageView.setImageBitmap(null);
                            imageView.setBackgroundResource(R.drawable.images);

                        }

                        Toast.makeText(UserUploadImagesActivity.this, msg, Toast.LENGTH_SHORT).show();

                        loadingDialog.dismiss();
//                        showDetectionDetails(plant, disease);

                    } catch (JSONException e) {
                        loadingDialog.dismiss();
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loadingDialog.dismiss();
                    Toast.makeText(UserUploadImagesActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });


            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(jsonObject);

        } else {
            Toast.makeText(UserUploadImagesActivity.this, "Capture or select image to detect.", Toast.LENGTH_SHORT).show();
        }

    }

}