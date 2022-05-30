package com.licencetracker.licencetracker.User;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.licencetracker.licencetracker.API.API;
import com.licencetracker.licencetracker.Classes.Preferences;
import com.licencetracker.licencetracker.LoginActivity;
import com.licencetracker.licencetracker.R;
import com.licencetracker.licencetracker.RegistrationActivity;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

public class UserFineDetailsActivity extends AppCompatActivity {

    private EditText date, vehicle, location, offence, amount, oname, odesignation;
    private Button btnPay;
    private TextView linkGoBack;

    private String fineAmount = "";

    private String id = "";


    public static final String CONFIG_CLIENT_ID = "AX0fgkqhn_f_nCEKy2NXa1U18M5RgPsml2MMKOouR6Str7skl7dNu_CTUujqY7A5--Pg-Omuku-XrY2y";
    private static final String TAG = "Licence-Tracker";
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .merchantName("Example Merchant")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_fine_details);

        Intent project = getIntent();
        id = project.getStringExtra("id");

        date = this.findViewById(R.id.date);
        vehicle = this.findViewById(R.id.vehicle);
        location = this.findViewById(R.id.location);
        offence = this.findViewById(R.id.offence);
        amount = this.findViewById(R.id.amount);
        oname = this.findViewById(R.id.oname);
        odesignation = this.findViewById(R.id.odesignation);

        btnPay = this.findViewById(R.id.btnPay);

        linkGoBack = this.findViewById(R.id.linkGoBack);

        getFineDetails();

        linkGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UserFineDetailsActivity.this, UserFinesListActivity.class);
                startActivity(intent);
                finish();

            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPayment();
            }
        });


    }


    private void getFineDetails() {

        String URL = API.FINE_API  + Preferences.LOGGED_USER_ID;

        RequestQueue requestQueue = Volley.newRequestQueue(UserFineDetailsActivity.this);
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

                                String dateValue = (String) responseData.get(3).toString();
                                String vehicleValue = (String) responseData.get(7).toString();
                                String locationValue = (String) responseData.get(6).toString();
                                String offenceValue = (String) responseData.get(4).toString();
                                String amountValue = (String) responseData.get(5).toString();
                                String onameValue = (String) responseData.get(21).toString();
                                String odesignationValue = (String) responseData.get(25).toString();

                                fineAmount = amountValue;

                                date.setText(dateValue);
                                vehicle.setText(vehicleValue);
                                location.setText(locationValue);
                                offence.setText(offenceValue);
                                amount.setText(amountValue);
                                oname.setText(onameValue);
                                odesignation.setText(odesignationValue);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserFineDetailsActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }

    private void getPayment() {

        if (fineAmount.equals("")){
            Toast.makeText(UserFineDetailsActivity.this, "Waiting for fine amount!", Toast.LENGTH_SHORT).show();
        } else {

            PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal(String.valueOf(fineAmount)),
                    "USD", "Licence Tracker | Pay fine",
                    PayPalPayment.PAYMENT_INTENT_SALE);
            Intent intent = new Intent(UserFineDetailsActivity.this, PaymentActivity.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
            startActivityForResult(intent, REQUEST_CODE_PAYMENT);

        }

    }

    protected void displayResultText(String result) {
        Toast.makeText(
                getApplicationContext(),
                result, Toast.LENGTH_LONG)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));

                        updatePaidStatus();

//                        Toast.makeText(this, "Fine Payment Success", Toast.LENGTH_SHORT).show();


                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                displayResultText("Payment canceled!");

            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("FuturePaymentExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        displayResultText("Future Payment code received from PayPal");

                    } catch (JSONException e) {
                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");

            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");

            }
        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("ProfileSharingExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("ProfileSharingExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        displayResultText("Profile Sharing code received from PayPal");

                    } catch (JSONException e) {
                        Log.e("ProfileSharingExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("ProfileSharingExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "ProfileSharingExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) { }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }


    private void updatePaidStatus() {

        try {
            String URL = API.FINE_API + "pay";

            RequestQueue requestQueue = Volley.newRequestQueue(UserFineDetailsActivity.this);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("id", id);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");
                        String msg = jsonObject.getString("msg");

                        Toast.makeText(UserFineDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(UserFineDetailsActivity.this, UserFinesListActivity.class);
                        startActivity(intent);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(UserFineDetailsActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        return null;
                    }
                }

            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}