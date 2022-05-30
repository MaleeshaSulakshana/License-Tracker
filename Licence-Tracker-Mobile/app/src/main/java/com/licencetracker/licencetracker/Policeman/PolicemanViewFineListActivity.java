package com.licencetracker.licencetracker.Policeman;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.licencetracker.licencetracker.API.API;
import com.licencetracker.licencetracker.Classes.Fine;
import com.licencetracker.licencetracker.Classes.FineAdapter;
import com.licencetracker.licencetracker.Classes.Preferences;
import com.licencetracker.licencetracker.R;
import com.licencetracker.licencetracker.User.UserFineDetailsActivity;
import com.licencetracker.licencetracker.User.UserFinesListActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class PolicemanViewFineListActivity extends AppCompatActivity {

    private ListView listFines;
    private ArrayList<Fine> detailsArrayList = new ArrayList<>();

    private String driverId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policeman_view_fine_list);

        Intent project = getIntent();
        driverId = project.getStringExtra("driverId");

        listFines = this.findViewById(R.id.listFines);

        showFines();

        listFines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                String selected = String.valueOf(detailsArrayList.get(i).getId());

                Intent intent = new Intent(PolicemanViewFineListActivity.this, PolicemanViewFineDetailsActivity.class);
                intent.putExtra("id", selected);
                startActivity(intent);

            }
        });

    }


    private void showFines()
    {
        detailsArrayList.clear();
        listFines.setAdapter(null);

        FineAdapter fineAdapter = new FineAdapter(this, R.layout.row_fine_item, detailsArrayList);
        listFines.setAdapter(fineAdapter);

        String URL = API.FINE_API + "driver/" + driverId;

        RequestQueue requestQueue = Volley.newRequestQueue(PolicemanViewFineListActivity.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {

                            for (int index = 0; index < response.length(); index++) {

                                JSONArray responseData = response.getJSONArray(index);

                                Integer id = (Integer) responseData.get(0);
                                String dateTime = (String) responseData.get(3);
                                String amount = (String) responseData.get(5);
                                String location = (String) responseData.get(6);

                                detailsArrayList.add(new Fine(id, dateTime.toString(), amount, location));

                            }

                            fineAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PolicemanViewFineListActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }

}